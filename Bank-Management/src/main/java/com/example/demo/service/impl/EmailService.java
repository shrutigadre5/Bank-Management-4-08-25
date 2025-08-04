package com.example.demo.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendLoginConfirmationEmail(String toEmail, String customerName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Login Successful - Banking App");
        message.setText("Dear " + customerName + ",\n\nYou have successfully logged in to your account.\n\nIf this wasn't you, please contact support immediately.\n\nBest regards,\nBanking Team");
        message.setFrom("your-email@gmail.com");

        mailSender.send(message);
    }
}
