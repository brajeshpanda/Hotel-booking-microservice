package com.notificationservice.Controller;



import com.notificationservice.Entity.EmailLog;
import com.notificationservice.Repository.EmailLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/email-logs")
public class AdminEmailLogController {

    @Autowired
    private EmailLogRepository emailLogRepository;

    @GetMapping
    public List<EmailLog> getAllLogs() {
        return emailLogRepository.findAll();
    }
}

