package com.JavaCSA.credit_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * EmailService provides functionality to send emails using the JavaMailSender interface provided by Spring.
 * It encapsulates the configuration and sending of simple email messages.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();  // Creates a new email message object.
        message.setFrom("no-reply@example.com");  // Sets the sender's email address.
        message.setTo(to);  // Sets the recipient's email address.
        message.setSubject(subject);  // Sets the subject line of the email.
        message.setText(text);  // Sets the main text content of the email.
        emailSender.send(message);  // Sends the email through the JavaMailSender infrastructure.
    }
}
