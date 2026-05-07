package com.internship.tool.service;

import com.internship.tool.entity.MeetingMinutes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface MeetingMinutesService {

    Page<MeetingMinutes> getAll(Pageable pageable);

    MeetingMinutes getById(Long id);

    MeetingMinutes create(MeetingMinutes meetingMinutes);

    MeetingMinutes update(Long id, MeetingMinutes meetingMinutes);

    void delete(Long id);

    Page<MeetingMinutes> search(String query, Pageable pageable);

    Page<MeetingMinutes> filterByStatus(String status, Pageable pageable);

    Page<MeetingMinutes> filterByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
