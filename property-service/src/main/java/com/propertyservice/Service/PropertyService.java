package com.propertyservice.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.propertyservice.Client.NotificationClient;
import com.propertyservice.Payload.EmailRequest;
import com.propertyservice.Payload.PaginationResponse;
import com.propertyservice.Redis.PropertyNotification;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.propertyservice.Entity.*;
import com.propertyservice.Payload.*;
import com.propertyservice.Repository.*;

@Service
public class PropertyService {

    @Autowired private PropertyRepository propertyRepository;
    @Autowired private AreaRepository areaRepository;
    @Autowired private CityRepository cityRepository;
    @Autowired private StateRepository stateRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private RoomAvailabilityRepository availabilityRepository;
    @Autowired private S3Service s3Service;
    @Autowired private PropertyPhotosRepository photosRepository;
    @Autowired private PropertyNotificationService notificationService;

    @Autowired
    private NotificationClient notificationClient;


    @Transactional
    public PropertyDto addProperty(PropertyDto dto, MultipartFile[] files) {

        Area area = areaRepository.findByName(dto.getArea());
        City city = cityRepository.findByName(dto.getCity());
        State state = stateRepository.findByName(dto.getState());

        Property property = new Property();
        property.setName(dto.getName());
        property.setNumberOfBathrooms(dto.getNumberOfBathrooms());
        property.setNumberOfBeds(dto.getNumberOfBeds());
        property.setNumberOfRooms(dto.getNumberOfRooms());
        property.setNumberOfGuestAllowed(dto.getNumberOfGuestAllowed());
        property.setArea(area);
        property.setCity(city);
        property.setState(state);
        property.setStatus("PENDING");
        property.setGstNumber(dto.getGstNumber());

        Property savedProperty = propertyRepository.save(property);

        // Save rooms
        for (RoomsDto roomsDto : dto.getRooms()) {
            Rooms room = new Rooms();
            room.setProperty(savedProperty);
            room.setRoomType(roomsDto.getRoomType());
            room.setBasePrice(roomsDto.getBasePrice());
            roomRepository.save(room);
        }

        // Redis pending
        PropertyNotification notification = new PropertyNotification(
                savedProperty.getId(),
                savedProperty.getName(),
                savedProperty.getGstNumber(),
                "PENDING"
        );
        notificationService.savePending(notification);

        // FEIGN Notify
        notificationClient.sendPropertyAdded(
                new EmailRequest(
                        "admin@gmail.com",
                        "New Property Added - Pending Approval",
                        "Property Name: " + savedProperty.getName() +
                                "\nGST: " + savedProperty.getGstNumber() +
                                "\nStatus: PENDING"
                )
        );

        // Upload photos
        List<String> urls = s3Service.uploadFiles(files);
        for (String url : urls) {
            PropertyPhotos p = new PropertyPhotos();
            p.setUrl(url);
            p.setProperty(savedProperty);
            photosRepository.save(p);
        }

        return convertToDto(savedProperty);
    }


    private PropertyDto convertToDto(Property p) {

        PropertyDto dto = new PropertyDto();
        BeanUtils.copyProperties(p, dto);

        dto.setArea(p.getArea().getName());
        dto.setCity(p.getCity().getName());
        dto.setState(p.getState().getName());
        dto.setStatus(p.getStatus());

        // Rooms
        List<RoomsDto> rooms = p.getRooms().stream().map(r -> {
            RoomsDto rd = new RoomsDto();
            BeanUtils.copyProperties(r, rd);
            return rd;
        }).collect(Collectors.toList());
        dto.setRooms(rooms);

        // Photos
        List<String> urls = p.getPhotos().stream()
                .map(PropertyPhotos::getUrl)
                .collect(Collectors.toList());
        dto.setImageUrls(urls);

        return dto;
    }

    public APIResponse<PropertyDto> findPropertyById(long id) {
        APIResponse<PropertyDto> response = new APIResponse<>();
        Optional<Property> opt = propertyRepository.findById(id);

        if (opt.isEmpty()) {
            response.setMessage("Not found");
            response.setStatus(404);
            return response;
        }

        response.setMessage("OK");
        response.setStatus(200);
        response.setData(convertToDto(opt.get()));
        return response;
    }


    public PaginationResponse<PropertyDto> searchProperty(
            String name, LocalDate date, int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Property> page = propertyRepository.searchPropertyPaged(name, date, pageable);

        List<PropertyDto> dtos = page.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PaginationResponse<>(dtos, pageNo, pageSize, (int) page.getTotalElements());
    }


    public PaginationResponse<RoomAvailability> getTotalRoomsAvailable(
            long id, int pageNo, int pageSize, String sortBy, String sortDir) {

        List<RoomAvailability> list = availabilityRepository.findByRoomId(id);

        list = list.stream()
                .sorted((a, b) -> {
                    Comparable v1 = getSortValue(a, sortBy);
                    Comparable v2 = getSortValue(b, sortBy);
                    return sortDir.equalsIgnoreCase("asc")
                            ? v1.compareTo(v2)
                            : v2.compareTo(v1);
                })
                .toList();

        int total = list.size();
        int start = pageNo * pageSize;
        int end = Math.min(start + pageSize, total);

        List<RoomAvailability> paginated = start < total ? list.subList(start, end) : List.of();

        return new PaginationResponse<>(paginated, pageNo, pageSize, total);
    }

    private Comparable<?> getSortValue(RoomAvailability a, String sortBy) {
        return switch (sortBy) {
            case "price" -> a.getPrice();
            case "availableCount" -> a.getAvailableCount();
            case "date" -> a.getAvailableDate();
            default -> a.getId();
        };
    }

    public Rooms getRoomById(long id) {
        return roomRepository.findById(id).orElse(null);
    }

    public APIResponse<Boolean> updateRoomCount(Long id, LocalDate date) {
        APIResponse<Boolean> res = new APIResponse<>();

        try {
            RoomAvailability ra = availabilityRepository.getRooms(id, date);

            if (ra == null) {
                res.setMessage("Not found");
                res.setStatus(404);
                res.setData(false);
                return res;
            }

            if (ra.getAvailableCount() > 0) {
                ra.setAvailableCount(ra.getAvailableCount() - 1);
                availabilityRepository.save(ra);
                res.setMessage("Updated");
                res.setStatus(200);
                res.setData(true);
            } else {
                res.setMessage("No rooms");
                res.setStatus(400);
                res.setData(false);
            }

        } catch (Exception e) {
            res.setMessage("Error");
            res.setStatus(500);
            res.setData(false);
        }

        return res;
    }

    // ====================================================================
    // ⭐ NEW — Required by BookingService
    // ====================================================================
    public List<RoomAvailability> getRoomAvailabilityList(long roomId) {
        return availabilityRepository.findByRoomId(roomId);
    }

    // ====================================================================
    // APPROVE PROPERTY
    // ====================================================================
    public APIResponse<String> approveProperty(long id) {

        Optional<Property> opt = propertyRepository.findById(id);

        if (opt.isEmpty()) {
            APIResponse<String> res = new APIResponse<>();
            res.setMessage("Property not found");
            res.setStatus(404);
            return res;
        }

        Property property = opt.get();
        property.setStatus("APPROVED");
        propertyRepository.save(property);

        notificationService.updateStatus(id, "APPROVED");

        notificationClient.sendPropertyApproved(
                new EmailRequest(
                        "owner@gmail.com",
                        "Property Approved",
                        "Your property '" + property.getName() + "' is now approved."
                )
        );

        APIResponse<String> response = new APIResponse<>();
        response.setMessage("Approved");
        response.setStatus(200);
        response.setData("APPROVED");

        return response;
    }
}
