package com.notificationservice.Entity;


import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "email_logs")
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String toEmail;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, length = 2000)
    private String body;

    @Temporal(TemporalType.TIMESTAMP)
    private Date sentAt = new Date();

    // SUCCESS / FAILED
    private String status;

    public EmailLog() {}

    public EmailLog(String toEmail, String subject, String body, String status) {
        this.toEmail = toEmail;
        this.subject = subject;
        this.body = body;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getToEmail() { return toEmail; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public Date getSentAt() { return sentAt; }
    public String getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setToEmail(String toEmail) { this.toEmail = toEmail; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setBody(String body) { this.body = body; }
    public void setSentAt(Date sentAt) { this.sentAt = sentAt; }
    public void setStatus(String status) { this.status = status; }
}

