package com.bookingservice.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bookingservice.Client.PaymentClient;
import com.bookingservice.Client.PropertyClient;
import com.bookingservice.Entity.BookingDate;
import com.bookingservice.Entity.Bookings;
import com.bookingservice.Payload.*;
import com.bookingservice.Repository.BookingDateRepository;
import com.bookingservice.Repository.BookingRepository;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    @Autowired
    private PropertyClient propertyClient;

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingDateRepository bookingDateRepository;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;

    @PostMapping("/add-to-cart")
    public APIResponse<List<String>> cart(@RequestBody BookingDto bookingDto) {

        Optional<RoomAvailability> matchedRoom = Optional.empty();

        APIResponse<List<String>> apiResponse = new APIResponse<>();
        List<String> messages = new ArrayList<>();

        // Fetch property
        APIResponse<PropertyDto> response = propertyClient.getPropertyById(bookingDto.getPropertyId());
        if (response.getData() == null) {
            apiResponse.setStatus(404);
            apiResponse.setMessage("Property not found");
            apiResponse.setData(List.of("Property not found"));
            return apiResponse;
        }

        // Fetch room
        APIResponse<Rooms> roomType = propertyClient.getRoomType(bookingDto.getRoomId());
        if (roomType.getData() == null) {
            apiResponse.setStatus(404);
            apiResponse.setMessage("Room type not found");
            apiResponse.setData(List.of("Room type not found"));
            return apiResponse;
        }

        // Fetch availability (updated)
        PaginationResponse<RoomAvailability> paginationResponse =
                propertyClient.getTotalRoomsAvailable(bookingDto.getRoomId());

        List<RoomAvailability> availableRooms = paginationResponse.getContent();

        if (availableRooms == null || availableRooms.isEmpty()) {
            apiResponse.setStatus(404);
            apiResponse.setMessage("No available rooms");
            apiResponse.setData(List.of("No available rooms found"));
            return apiResponse;
        }

        // Validate room availability for each date
        for (LocalDate date : bookingDto.getDate()) {

            boolean isAvailable = availableRooms.stream()
                    .anyMatch(ra -> ra.getAvailableDate().equals(date) && ra.getAvailableCount() > 0);

            if (!isAvailable) {
                apiResponse.setStatus(500);
                apiResponse.setMessage("Sold Out");
                apiResponse.setData(List.of("Room not available on: " + date));
                return apiResponse;
            }

            matchedRoom = availableRooms.stream()
                    .filter(ra -> ra.getAvailableDate().equals(date) && ra.getAvailableCount() > 0)
                    .findFirst();
        }

        // Save booking
        Bookings bookings = new Bookings();
        bookings.setName(bookingDto.getName());
        bookings.setEmail(bookingDto.getEmail());
        bookings.setMobile(bookingDto.getMobile());
        bookings.setPropertyName(response.getData().getName());
        bookings.setStatus("pending");
        bookings.setTotalPrice(roomType.getData().getBasePrice() * bookingDto.getDate().size());

        Bookings savedBooking = bookingRepository.save(bookings);

        // Save dates + decrement room
        for (LocalDate date : bookingDto.getDate()) {
            BookingDate bookingDate = new BookingDate();
            bookingDate.setDate(date);
            bookingDate.setBookings(savedBooking);
            bookingDateRepository.save(bookingDate);

            if (matchedRoom.isPresent()) {
                propertyClient.updateRoomCount(matchedRoom.get().getId(), date.format(dateFormatter));
            }
        }

        messages.add("Booking added to cart successfully. Booking ID: " + savedBooking.getId());
        apiResponse.setStatus(200);
        apiResponse.setMessage("Success");
        apiResponse.setData(messages);

        return apiResponse;
    }

    @PostMapping("/process-payment")
    public StripeResponse proceedPayment(@RequestBody ProductRequest productRequest) {
        return paymentClient.checkoutProducts(productRequest);
    }

    @PutMapping("/update-status-booking")
    public boolean updateBooking(@RequestParam long id) {
        Optional<Bookings> opBooking = bookingRepository.findById(id);
        if (opBooking.isPresent()) {
            Bookings bookings = opBooking.get();
            bookings.setStatus("confirmed");
            return bookingRepository.save(bookings) != null;
        }
        return false;
    }
}
