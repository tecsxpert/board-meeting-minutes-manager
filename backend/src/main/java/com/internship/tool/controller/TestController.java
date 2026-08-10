package com.internship.tool.controller;

import com.internship.tool.entity.MeetingMinutes;
import com.internship.tool.repository.MeetingMinutesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final MeetingMinutesRepository meetingMinutesRepository;

    @GetMapping("/data")
    public String testData() {
        long count = meetingMinutesRepository.count();
        long activeCount = meetingMinutesRepository.countByIsDeletedFalse();
        return String.format("Total records: %d, Active records: %d", count, activeCount);
    }

    @GetMapping("/meetings")
    public List<MeetingMinutes> getAllMeetings() {
        return meetingMinutesRepository.findAll();
    }

    @GetMapping("/active-meetings")
    public List<MeetingMinutes> getActiveMeetings() {
        return meetingMinutesRepository.findAllByIsDeletedFalse(org.springframework.data.domain.Pageable.unpaged()).getContent();
    }

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        return Map.of(
            "status", "UP",
            "timestamp", java.time.LocalDateTime.now(),
            "database", "Connected",
            "totalRecords", meetingMinutesRepository.count(),
            "activeRecords", meetingMinutesRepository.countByIsDeletedFalse()
        );
    }
}