package com.internship.tool.service;

import com.internship.tool.entity.MeetingMinutes;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.exception.ValidationException;
import com.internship.tool.repository.MeetingMinutesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MeetingMinutesServiceImpl implements MeetingMinutesService {

    private final MeetingMinutesRepository repository;

    @Override
    public Page<MeetingMinutes> getAll(Pageable pageable) {
        return repository.findAllByIsDeletedFalse(pageable);
    }

    @Override
    public MeetingMinutes getById(Long id) {
        return repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meeting minutes not found with id: " + id));
    }

    @Override
    public MeetingMinutes create(MeetingMinutes meetingMinutes) {
        validateMeetingMinutes(meetingMinutes);
        meetingMinutes.setIsDeleted(false);
        return repository.save(meetingMinutes);
    }

    @Override
    public MeetingMinutes update(Long id, MeetingMinutes updated) {
        validateMeetingMinutes(updated);
        MeetingMinutes existing = getById(id);
        existing.setTitle(updated.getTitle());
        existing.setMeetingDate(updated.getMeetingDate());
        existing.setAttendees(updated.getAttendees());
        existing.setAgenda(updated.getAgenda());
        existing.setContent(updated.getContent());
        existing.setStatus(updated.getStatus());
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        MeetingMinutes existing = getById(id);
        existing.setIsDeleted(true);
        repository.save(existing);
    }

    @Override
    public Page<MeetingMinutes> search(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            throw new ValidationException("Search query cannot be empty");
        }
        return repository.searchByTitleOrContent(query.trim(), pageable);
    }

    @Override
    public Page<MeetingMinutes> filterByStatus(String status, Pageable pageable) {
        if (status == null || status.trim().isEmpty()) {
            throw new ValidationException("Status cannot be empty");
        }
        return repository.findAllByStatusAndIsDeletedFalse(status.trim(), pageable);
    }

    @Override
    public Page<MeetingMinutes> filterByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (startDate == null || endDate == null) {
            throw new ValidationException("Start date and end date are required");
        }
        if (startDate.isAfter(endDate)) {
            throw new ValidationException("Start date cannot be after end date");
        }
        return repository.findAllByMeetingDateBetweenAndIsDeletedFalse(startDate, endDate, pageable);
    }

    // ─── Input Validation ─────────────────────────────────────────────────────

    private void validateMeetingMinutes(MeetingMinutes m) {
        if (m.getTitle() == null || m.getTitle().trim().isEmpty()) {
            throw new ValidationException("Title is required");
        }
        if (m.getTitle().length() > 255) {
            throw new ValidationException("Title must not exceed 255 characters");
        }
        if (m.getMeetingDate() == null) {
            throw new ValidationException("Meeting date is required");
        }
        if (m.getContent() == null || m.getContent().trim().isEmpty()) {
            throw new ValidationException("Content is required");
        }
        if (m.getStatus() == null || m.getStatus().trim().isEmpty()) {
            throw new ValidationException("Status is required");
        }
    }
}
