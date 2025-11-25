package com.paymentservice.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.paymentservice.Client.BookingClient;
import com.paymentservice.Client.NotificationClient;
import com.paymentservice.Payload.EmailRequest;
import com.paymentservice.Payload.ProductRequest;
import com.paymentservice.Payload.StripeResponse;
import com.paymentservice.Service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

@RestController
@RequestMapping("/product/v1")
public class ProductCheckoutController {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private BookingClient bookingClient;

    @Autowired
    private NotificationClient notificationClient;

    @PostMapping("/checkout")
    public StripeResponse checkoutProducts(@RequestBody ProductRequest productRequest) {
        return stripeService.checkoutProducts(productRequest);
    }

    // Stripe Redirect SUCCESS URL
    @GetMapping("/success")
    public String handleSuccess(
            @RequestParam("session_id") String sessionId,
            @RequestParam("booking_id") long bookingId) {

        try {
            Session session = Session.retrieve(sessionId);

            if ("paid".equalsIgnoreCase(session.getPaymentStatus())) {

                boolean result = bookingClient.updateBooking(bookingId);

                if (result) {

                    // ⭐ 1️⃣ SEND DIRECT EMAIL TO CUSTOMER
                    notificationClient.sendEmail(
                            new EmailRequest(
                                    "customer@gmail.com",
                                    "Booking Confirmed",
                                    "Your booking has been successfully confirmed.\nBooking ID: " + bookingId
                            )
                    );

                    System.out.println("📩 Booking confirmation email sent");
                }

                return "Payment successful & booking updated";
            }

            return "Payment not completed";

        } catch (StripeException e) {
            e.printStackTrace();
            return "Stripe Error: " + e.getMessage();
        }
    }

    @GetMapping("/cancel")
    public String handleCancel() {
        return "Payment cancelled";
    }
}
