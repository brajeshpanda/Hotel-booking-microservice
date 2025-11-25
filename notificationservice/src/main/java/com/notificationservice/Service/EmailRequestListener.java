package com.notificationservice.Service;

import com.notificationservice.Constants.AppConstants;
import com.notificationservice.Entity.EmailLog;
import com.notificationservice.Payload.EmailRequest;
import com.notificationservice.Repository.EmailLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailRequestListener {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailLogRepository emailLogRepository;

    @KafkaListener(
            topics = AppConstants.TOPIC,
            groupId = "notification-group",
            containerFactory = "kafkaListenerFactory"
    )
    public void kafkaSubscriberContent(EmailRequest emailContent) {

        String status = "SUCCESS";

        try {
            SimpleMailMessage sm = new SimpleMailMessage();
            sm.setTo(emailContent.getTo());
            sm.setSubject(emailContent.getSubject());
            sm.setText(emailContent.getBody());

            javaMailSender.send(sm);
            System.out.println("EMAIL SENT → " + emailContent.getTo());
        }
        catch (Exception e) {
            status = "FAILED";
            System.out.println(" EMAIL FAILED → " + emailContent.getTo());
        }

        // ---- SAVE EMAIL LOG TO DATABASE ----
        EmailLog log = new EmailLog(
                emailContent.getTo(),
                emailContent.getSubject(),
                emailContent.getBody(),
                status
        );

        emailLogRepository.save(log);
        System.out.println(" Log saved to DB");
    }
}
