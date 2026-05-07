package com.internship.tool.service;

import com.internship.tool.entity.User;
import com.internship.tool.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledEmailService {

    private final EmailService emailService;
    private final UserRepository userRepository;

    // Daily reminder — runs every day at 9:00 AM
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailyReminders() {
        log.info("Sending daily reminders...");
        List<User> users = userRepository.findAll();
        for (User user : users) {
            emailService.sendDailyReminder(user.getEmail(), user.getUsername());
        }
        log.info("Daily reminders sent to {} users", users.size());
    }

    // Deadline alert — runs every day at 5:00 PM
    @Scheduled(cron = "0 0 17 * * *")
    public void sendDeadlineAlerts() {
        log.info("Sending deadline alerts...");
        List<User> users = userRepository.findAll();
        for (User user : users) {
            emailService.sendDeadlineAlert(user.getEmail(), user.getUsername(),
                    "Pending Meeting Minutes");
        }
        log.info("Deadline alerts sent to {} users", users.size());
    }
}
