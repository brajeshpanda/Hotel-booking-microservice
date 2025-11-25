package com.notificationservice.Controller;

import com.notificationservice.Payload.EmailRequest;
import com.notificationservice.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notify")
public class NotificationController {

    @Autowired
    private EmailService emailService;

    // -------- SEND EMAIL ON PROPERTY ADDED --------
    @PostMapping("/property-added")
    public String propertyAdded(@RequestBody EmailRequest request) {
        emailService.sendEmail(request);
        return "Email sent for Property Added";
    }

    // -------- SEND EMAIL ON PROPERTY APPROVED --------
    @PostMapping("/property-approved")
    public String propertyApproved(@RequestBody EmailRequest request) {
        emailService.sendEmail(request);
        return "Email sent for Property Approved";
    }
    @PostMapping("/booking-confirmed")
    public String bookingConfirmed(@RequestBody EmailRequest request) {
        emailService.sendEmail(request);
        return "Booking confirmation email sent";
    }
}
