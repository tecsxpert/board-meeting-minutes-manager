package com.internship.tool.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // Send daily reminder email
    public void sendDailyReminder(String toEmail, String recipientName) {
        try {
            Context context = new Context();
            context.setVariable("recipientName", recipientName);

            String htmlContent = templateEngine.process("reminder-email", context);
            sendHtmlEmail(toEmail, "Daily Meeting Minutes Reminder", htmlContent);

        } catch (Exception e) {
            log.error("Failed to send daily reminder to {}: {}", toEmail, e.getMessage());
        }
    }

    // Send deadline alert email
    public void sendDeadlineAlert(String toEmail, String recipientName, String meetingTitle) {
        try {
            Context context = new Context();
            context.setVariable("recipientName", recipientName);
            context.setVariable("meetingTitle", meetingTitle);

            String htmlContent = templateEngine.process("deadline-alert-email", context);
            sendHtmlEmail(toEmail, "Meeting Minutes Deadline Alert", htmlContent);

        } catch (Exception e) {
            log.error("Failed to send deadline alert to {}: {}", toEmail, e.getMessage());
        }
    }

    // Core method to send HTML email
    private void sendHtmlEmail(String to, String subject, String htmlContent)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
        log.info("Email sent to {}", to);
    }
}
