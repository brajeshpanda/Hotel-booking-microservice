package com.authservice.Service;

import com.authservice.Payload.EmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailProducer {

    private static final String TOPIC = "send-email";

    @Autowired
    private KafkaTemplate<String, EmailRequest> kafkaTemplate;


    public void sendLoginEmail(String email, String name) {

        EmailRequest request = new EmailRequest(
                email,
                "Login Successful",
                "Hi " + name + ", You logged in successfully!"
        );

        kafkaTemplate.send(TOPIC, request);
        System.out.println(" Login email event published to Kafka topic: " + TOPIC);
    }


    public void sendRegisterOtp(String email, String otp) {

        EmailRequest request = new EmailRequest(
                email,
                "Your OTP for Registration",
                "Your OTP is: " + otp
        );

        kafkaTemplate.send(TOPIC, request);
        System.out.println(" Registration OTP email published to Kafka topic: " + TOPIC);
    }

    public void sendForgotPasswordOtp(String email, String otp) {

        EmailRequest request = new EmailRequest(
                email,
                "Password Reset OTP",
                "Your OTP to reset password is: " + otp
        );

        kafkaTemplate.send(TOPIC, request);
        System.out.println(" Forgot password OTP email published to Kafka!");
    }

    public void sendPasswordResetSuccess(String email) {

        EmailRequest request = new EmailRequest(
                email,
                "Password Reset Successful",
                "Your password has been successfully updated."
        );

        kafkaTemplate.send(TOPIC, request);
        System.out.println("📨 Password reset confirmation email published!");
    }
    // 5️⃣ OTP LOGIN EMAIL
    public void sendLoginOtp(String email, String otp) {

        EmailRequest request = new EmailRequest(
                email,
                "Your OTP for Login",
                "Your login OTP is: " + otp
        );

        kafkaTemplate.send(TOPIC, request);
        System.out.println("📨 Login OTP email published!");
    }


}
