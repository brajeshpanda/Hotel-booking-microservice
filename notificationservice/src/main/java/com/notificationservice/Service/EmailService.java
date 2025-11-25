package com.notificationservice.Service;

import com.notificationservice.Payload.EmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(EmailRequest request) {

        SimpleMailMessage sm = new SimpleMailMessage();
        sm.setTo(request.getTo());
        sm.setSubject(request.getSubject());
        sm.setText(request.getBody());

        javaMailSender.send(sm);
        System.out.println("EMAIL SENT → " + request.getTo());
    }
}
