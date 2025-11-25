package com.bookingservice.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookingservice.Payload.APIResponse;
import com.bookingservice.Payload.PaginationResponse;
import com.bookingservice.Payload.PropertyDto;
import com.bookingservice.Payload.RoomAvailability;
import com.bookingservice.Payload.Rooms;

@FeignClient(name = "PROPERTY-SERVICE")
public interface PropertyClient {

    @GetMapping("/api/v1/property/property-id")
    APIResponse<PropertyDto> getPropertyById(@RequestParam("id") long id);

    @GetMapping("/api/v1/property/room-available-room-id")
    PaginationResponse<RoomAvailability> getTotalRoomsAvailable(@RequestParam("id") long id);

    @GetMapping("/api/v1/property/room-id")
    APIResponse<Rooms> getRoomType(@RequestParam("id") long id);

    @PutMapping("/api/v1/property/updateRoomCount")
    APIResponse<Boolean> updateRoomCount(@RequestParam("id") Long id,
                                         @RequestParam("date") String date);
}
