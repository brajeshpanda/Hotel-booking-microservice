package com.propertyservice.Client;
import com.propertyservice.Payload.EmailRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE")  // Using Eureka Service Name
public interface NotificationClient {

    @PostMapping("/api/v1/notify/property-added")
    String sendPropertyAdded(@RequestBody EmailRequest request);

    @PostMapping("/api/v1/notify/property-approved")
    String sendPropertyApproved(@RequestBody EmailRequest request);
}



