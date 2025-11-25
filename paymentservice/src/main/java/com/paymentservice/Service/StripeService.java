package com.paymentservice.Service;

import com.paymentservice.Payload.ProductRequest;
import com.paymentservice.Payload.StripeResponse;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    @Value("${stripe.successUrl}")
    private String successUrl;

    @Value("${stripe.cancelUrl}")
    private String cancelUrl;

    public StripeResponse checkoutProducts(ProductRequest productRequest) {

        Stripe.apiKey = secretKey;

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}&booking_id=" + productRequest.getBookingid())
                    .setCancelUrl(cancelUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(productRequest.getQuantity())
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(productRequest.getCurrency())
                                                    .setUnitAmount(productRequest.getAmount())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(productRequest.getName())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);

            StripeResponse res = new StripeResponse();
            res.setStatus("SUCCESS");
            res.setMessage("Session created");
            res.setSessionId(session.getId());
            res.setSessionUrl(session.getUrl());
            return res;

        } catch (Exception e) {
            StripeResponse res = new StripeResponse();
            res.setStatus("FAILED");
            res.setMessage("Error: " + e.getMessage());
            return res;
        }
    }
}
