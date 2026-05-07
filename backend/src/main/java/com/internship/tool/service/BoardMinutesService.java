// BoardMinutesService.java: Core business logic for managing board meeting minutes.
// Handles CRUD, soft delete, search, stats, CSV export, and file upload operations.

package com.internship.tool.service;

import com.internship.tool.annotation.Auditable;
import com.internship.tool.dto.BoardMinutesDto;
import com.internship.tool.dto.BoardMinutesResponseDto;
import com.internship.tool.dto.StatsDto;
import com.internship.tool.entity.BoardMinutes;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.repository.BoardMinutesRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BoardMinutesService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final String UPLOAD_DIR   = "./uploads/";

    private final BoardMinutesRepository repository;

    // ─── Read Operations ─────────────────────────────────────────────────────

    /**
     * Returns a paginated list of all non-deleted board minutes.
     */
    public Page<BoardMinutesResponseDto> getAll(Pageable pageable) {
        return repository.findAllByDeletedAtIsNull(pageable)
                .map(BoardMinutesResponseDto::from);
    }

    /**
     * Returns a single board minutes record by ID, or throws 404.
     */
    public BoardMinutesResponseDto getById(UUID id) {
        BoardMinutes entity = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("BoardMinutes", "id", id));
        return BoardMinutesResponseDto.from(entity);
    }

    // ─── Write Operations ─────────────────────────────────────────────────────

    /**
     * Creates and persists a new board minutes record.
     */
    @Transactional
    @Auditable(entityType = "BoardMinutes", action = "CREATE")
    public BoardMinutesResponseDto create(BoardMinutesDto dto) {
        BoardMinutes entity = mapToEntity(new BoardMinutes(), dto);
        BoardMinutes saved = repository.save(entity);
        return BoardMinutesResponseDto.from(saved);
    }

    /**
     * Updates an existing board minutes record, throws 404 if not found.
     */
    @Transactional
    @Auditable(entityType = "BoardMinutes", action = "UPDATE")
    public BoardMinutesResponseDto update(UUID id, BoardMinutesDto dto) {
        BoardMinutes existing = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("BoardMinutes", "id", id));
        mapToEntity(existing, dto);
        return BoardMinutesResponseDto.from(repository.save(existing));
    }

    /**
     * Soft-deletes a board minutes record by setting deleted_at, throws 404 if not found.
     */
    @Transactional
    @Auditable(entityType = "BoardMinutes", action = "DELETE")
    public void softDelete(UUID id) {
        BoardMinutes existing = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("BoardMinutes", "id", id));
        existing.setDeletedAt(LocalDateTime.now());
        repository.save(existing);
    }

    // ─── Search ──────────────────────────────────────────────────────────────

    /**
     * Case-insensitive full-text search across title and content.
     */
    public Page<BoardMinutesResponseDto> search(String keyword, Pageable pageable) {
        return repository.searchByKeyword(keyword, pageable)
                .map(BoardMinutesResponseDto::from);
    }

    // ─── Stats ───────────────────────────────────────────────────────────────

    /**
     * Returns KPI counts: total, published, draft, archived (excluding soft-deleted).
     */
    public StatsDto getStats() {
        List<Object[]> rows = repository.countGroupedByStatus();

        long draft = 0, published = 0, archived = 0;
        for (Object[] row : rows) {
            BoardMinutes.Status status = (BoardMinutes.Status) row[0];
            long count = (Long) row[1];
            switch (status) {
                case DRAFT     -> draft     = count;
                case PUBLISHED -> published = count;
                case ARCHIVED  -> archived  = count;
            }
        }
        long total = draft + published + archived;
        return new StatsDto(total, published, draft, archived);
    }

    // ─── CSV Export ──────────────────────────────────────────────────────────

    /**
     * Writes all non-deleted board minutes as a CSV file to the HTTP response output stream.
     */
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"board_minutes.csv\"");

        List<BoardMinutes> all = repository.findAllForExport();

        try (PrintWriter writer = response.getWriter()) {
            writer.println("id,title,meeting_date,status,attendees");
            for (BoardMinutes m : all) {
                writer.printf("%s,%s,%s,%s,%s%n",
                        m.getId(),
                        escapeCsv(m.getTitle()),
                        m.getMeetingDate(),
                        m.getStatus(),
                        escapeCsv(m.getAttendees()));
            }
        }
    }

    // ─── File Upload ─────────────────────────────────────────────────────────

    /**
     * Validates file type (PDF or DOCX) and size (<= 5MB), then saves to ./uploads/ directory.
     * Returns the stored filename.
     */
    @Transactional
    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the 5MB limit");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null ||
                (!originalFilename.toLowerCase().endsWith(".pdf") &&
                 !originalFilename.toLowerCase().endsWith(".docx"))) {
            throw new IllegalArgumentException("Only PDF and DOCX files are allowed");
        }

        // Build unique filename to avoid collisions
        String storedFilename = UUID.randomUUID() + "_" + originalFilename;
        Path uploadPath = Paths.get(UPLOAD_DIR);
        Files.createDirectories(uploadPath);
        Path destination = uploadPath.resolve(storedFilename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        log.info("Uploaded file saved to: {}", destination.toAbsolutePath());
        return storedFilename;
    }

    // ─── Private Helpers ─────────────────────────────────────────────────────

    private BoardMinutes mapToEntity(BoardMinutes entity, BoardMinutesDto dto) {
        entity.setTitle(dto.getTitle());
        entity.setMeetingDate(dto.getMeetingDate());
        entity.setAttendees(dto.getAttendees());
        entity.setContent(dto.getContent());
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        } else if (entity.getStatus() == null) {
            entity.setStatus(BoardMinutes.Status.DRAFT);
        }
        entity.setAiDescription(dto.getAiDescription());
        entity.setAiRecommendations(dto.getAiRecommendations());
        return entity;
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
