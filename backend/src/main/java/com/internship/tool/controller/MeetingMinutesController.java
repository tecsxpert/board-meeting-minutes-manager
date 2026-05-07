package com.internship.tool.controller;

import com.internship.tool.entity.MeetingMinutes;
import com.internship.tool.repository.MeetingMinutesRepository;
import com.internship.tool.service.MeetingMinutesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/minutes")
@RequiredArgsConstructor
public class MeetingMinutesController {

    private final MeetingMinutesService service;
    private final MeetingMinutesRepository repository;

    // GET /api/minutes?page=0&size=10
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<MeetingMinutes>> getAll(Pageable pageable) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    // GET /api/minutes/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<MeetingMinutes> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // POST /api/minutes
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<MeetingMinutes> create(@Valid @RequestBody MeetingMinutes meetingMinutes) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(meetingMinutes));
    }

    // PUT /api/minutes/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<MeetingMinutes> update(
            @PathVariable Long id,
            @Valid @RequestBody MeetingMinutes meetingMinutes) {
        return ResponseEntity.ok(service.update(id, meetingMinutes));
    }

    // DELETE /api/minutes/{id} — soft delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/minutes/search?q=keyword
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<MeetingMinutes>> search(
            @RequestParam String q,
            Pageable pageable) {
        return ResponseEntity.ok(service.search(q, pageable));
    }

    // GET /api/minutes/filter/status?status=DRAFT
    @GetMapping("/filter/status")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<MeetingMinutes>> filterByStatus(
            @RequestParam String status,
            Pageable pageable) {
        return ResponseEntity.ok(service.filterByStatus(status, pageable));
    }

    // GET /api/minutes/filter/date-range?start=2026-01-01&end=2026-12-31
    @GetMapping("/filter/date-range")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<MeetingMinutes>> filterByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Pageable pageable) {
        return ResponseEntity.ok(service.filterByDateRange(start, end, pageable));
    }

    // GET /api/minutes/stats — dashboard KPIs
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getStats() {
        long total = repository.countByIsDeletedFalse();
        long published = repository.countByStatusAndIsDeletedFalse("PUBLISHED");
        long draft = repository.countByStatusAndIsDeletedFalse("DRAFT");
        long archived = repository.countByStatusAndIsDeletedFalse("ARCHIVED");

        return ResponseEntity.ok(Map.of(
                "totalMeetings", total,
                "publishedCount", published,
                "draftCount", draft,
                "archivedCount", archived
        ));
    }
}
