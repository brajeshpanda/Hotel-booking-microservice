package com.propertyservice.Controller;

import java.time.LocalDate;

import com.propertyservice.Payload.PaginationResponse;
import com.propertyservice.Redis.PropertyNotification;
import com.propertyservice.Service.PropertyNotificationService;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.propertyservice.Entity.RoomAvailability;
import com.propertyservice.Entity.Rooms;
import com.propertyservice.Payload.APIResponse;
import com.propertyservice.Payload.PropertyDto;
import com.propertyservice.Service.PropertyService;

import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/v1/property")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private PropertyNotificationService notificationService;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PropertyController.class);


    @PostMapping(
            value = "/add-property",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<APIResponse<PropertyDto>> addProperty(
            @RequestPart("property") PropertyDto dto,
            @RequestPart("files") MultipartFile[] files) {

        PropertyDto created = propertyService.addProperty(dto, files);

        APIResponse<PropertyDto> response = new APIResponse<>();
        response.setMessage("Property added");
        response.setStatus(201);
        response.setData(created);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }



    @GetMapping("/search-property")
    public ResponseEntity<PaginationResponse<PropertyDto>> searchProperty(
            @RequestParam String name,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        var response = propertyService.searchProperty(name, date, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/property-id")
    public APIResponse<PropertyDto> getPropertyById(@RequestParam long id) {
        return propertyService.findPropertyById(id);
    }



    @GetMapping("/room-available-room-id")
    public ResponseEntity<PaginationResponse<RoomAvailability>> getTotalRoomsAvailable(
            @RequestParam long id,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "availableCount") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        var response = propertyService.getTotalRoomsAvailable(id, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }


    // -------------------------------------------------------------
    // ⭐ NEW ENDPOINT — ROOM AVAILABILITY LIST (FOR BOOKING SERVICE)
    // -------------------------------------------------------------
    @GetMapping("/room-availability-list")
    public APIResponse<java.util.List<RoomAvailability>> getRoomAvailabilityList(@RequestParam long id) {

        APIResponse<java.util.List<RoomAvailability>> response = new APIResponse<>();
        response.setStatus(200);
        response.setMessage("OK");
        response.setData(propertyService.getRoomAvailabilityList(id));

        return response;
    }


    // -------------------------------------------------------------
    // GET ROOM DETAILS
    // -------------------------------------------------------------
    @GetMapping("/room-id")
    public APIResponse<Rooms> getRoomType(@RequestParam long id) {
        Rooms room = propertyService.getRoomById(id);

        APIResponse<Rooms> response = new APIResponse<>();
        response.setMessage("Room details");
        response.setStatus(200);
        response.setData(room);

        return response;
    }


    // -------------------------------------------------------------
    // UPDATE ROOM COUNT
    // -------------------------------------------------------------
    @PutMapping("/updateRoomCount")
    public APIResponse<Boolean> updateRoomCount(
            @RequestParam Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return propertyService.updateRoomCount(id, date);
    }


    // -------------------------------------------------------------
    // APPROVE PROPERTY
    // -------------------------------------------------------------
    @PutMapping("/approve")
    public ResponseEntity<APIResponse<String>> approveProperty(@RequestParam long id) {
        var response = propertyService.approveProperty(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    // -------------------------------------------------------------
    // PENDING LIST (REDIS)
    // -------------------------------------------------------------
    @GetMapping("/pending-list")
    public ResponseEntity<PaginationResponse<PropertyNotification>> getPendingList(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "propertyId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        var response = notificationService.getPendingPaginated(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }
}
