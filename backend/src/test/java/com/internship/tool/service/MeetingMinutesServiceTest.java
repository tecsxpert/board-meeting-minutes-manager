package com.internship.tool.service;

import com.internship.tool.entity.MeetingMinutes;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.exception.ValidationException;
import com.internship.tool.repository.MeetingMinutesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetingMinutesServiceTest {

    @Mock
    private MeetingMinutesRepository repository;

    @InjectMocks
    private MeetingMinutesServiceImpl service;

    private MeetingMinutes sampleMinutes;

    @BeforeEach
    void setUp() {
        sampleMinutes = MeetingMinutes.builder()
                .id(1L)
                .title("Q1 Board Meeting")
                .meetingDate(LocalDate.of(2026, 4, 14))
                .content("Discussion about Q1 results")
                .status("DRAFT")
                .isDeleted(false)
                .build();
    }

    // Test 1 — getAll returns paginated results
    @Test
    void getAll_ShouldReturnPageOfMinutes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MeetingMinutes> page = new PageImpl<>(List.of(sampleMinutes));
        when(repository.findAllByIsDeletedFalse(pageable)).thenReturn(page);

        Page<MeetingMinutes> result = service.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository, times(1)).findAllByIsDeletedFalse(pageable);
    }

    // Test 2 — getById returns correct record
    @Test
    void getById_ShouldReturnMinutes_WhenExists() {
        when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(sampleMinutes));

        MeetingMinutes result = service.getById(1L);

        assertNotNull(result);
        assertEquals("Q1 Board Meeting", result.getTitle());
    }

    // Test 3 — getById throws 404 when not found
    @Test
    void getById_ShouldThrowResourceNotFoundException_WhenNotFound() {
        when(repository.findByIdAndIsDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));
    }

    // Test 4 — create saves valid record
    @Test
    void create_ShouldSaveAndReturnMinutes_WhenValid() {
        when(repository.save(any(MeetingMinutes.class))).thenReturn(sampleMinutes);

        MeetingMinutes result = service.create(sampleMinutes);

        assertNotNull(result);
        assertEquals("Q1 Board Meeting", result.getTitle());
        verify(repository, times(1)).save(any(MeetingMinutes.class));
    }

    // Test 5 — create throws ValidationException when title is empty
    @Test
    void create_ShouldThrowValidationException_WhenTitleIsEmpty() {
        sampleMinutes.setTitle("");

        assertThrows(ValidationException.class, () -> service.create(sampleMinutes));
        verify(repository, never()).save(any());
    }

    // Test 6 — create throws ValidationException when content is null
    @Test
    void create_ShouldThrowValidationException_WhenContentIsNull() {
        sampleMinutes.setContent(null);

        assertThrows(ValidationException.class, () -> service.create(sampleMinutes));
        verify(repository, never()).save(any());
    }

    // Test 7 — update modifies existing record
    @Test
    void update_ShouldUpdateAndReturnMinutes_WhenValid() {
        MeetingMinutes updated = MeetingMinutes.builder()
                .title("Updated Title")
                .meetingDate(LocalDate.of(2026, 4, 15))
                .content("Updated content")
                .status("FINAL")
                .isDeleted(false)
                .build();

        when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(sampleMinutes));
        when(repository.save(any(MeetingMinutes.class))).thenReturn(updated);

        MeetingMinutes result = service.update(1L, updated);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
    }

    // Test 8 — delete soft deletes the record
    @Test
    void delete_ShouldSoftDeleteMinutes_WhenExists() {
        when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(sampleMinutes));
        when(repository.save(any(MeetingMinutes.class))).thenReturn(sampleMinutes);

        service.delete(1L);

        assertTrue(sampleMinutes.getIsDeleted());
        verify(repository, times(1)).save(sampleMinutes);
    }

    // Test 9 — search throws ValidationException when query is empty
    @Test
    void search_ShouldThrowValidationException_WhenQueryIsEmpty() {
        assertThrows(ValidationException.class,
                () -> service.search("", PageRequest.of(0, 10)));
    }

    // Test 10 — filterByDateRange throws ValidationException when startDate is after endDate
    @Test
    void filterByDateRange_ShouldThrowValidationException_WhenStartDateAfterEndDate() {
        LocalDate start = LocalDate.of(2026, 5, 1);
        LocalDate end = LocalDate.of(2026, 4, 1);

        assertThrows(ValidationException.class,
                () -> service.filterByDateRange(start, end, PageRequest.of(0, 10)));
    }
}
