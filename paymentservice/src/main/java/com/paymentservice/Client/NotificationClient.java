package com.paymentservice.Client;

import com.paymentservice.Payload.EmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationClient {

    @PostMapping("/api/v1/notify/booking-confirmed")
    void sendEmail(@RequestBody EmailRequest emailRequest);
}
