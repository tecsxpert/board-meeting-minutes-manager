package com.internship.tool.controller;

import com.internship.tool.entity.BoardMeetingMinutes;
import com.internship.tool.service.MeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
@Tag(name = "Meetings", description = "Board Meeting Minutes CRUD + Export + Upload")
public class MeetingController {

    private final MeetingService meetingService;

    @Operation(summary = "Get all meetings (paginated)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List returned successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<Page<BoardMeetingMinutes>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(meetingService.getAllMeetings(PageRequest.of(page, size)));
    }

    @Operation(summary = "Get meeting by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Meeting found"),
        @ApiResponse(responseCode = "404", description = "Meeting not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BoardMeetingMinutes> getById(@PathVariable Long id) {
        return meetingService.getMeetingById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new meeting")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Meeting created"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<BoardMeetingMinutes> create(
            @Valid @RequestBody BoardMeetingMinutes meeting) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(meetingService.createMeeting(meeting));
    }

    @Operation(summary = "Update a meeting")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Meeting updated"),
        @ApiResponse(responseCode = "404", description = "Meeting not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BoardMeetingMinutes> update(
            @PathVariable Long id,
            @Valid @RequestBody BoardMeetingMinutes meeting) {
        try {
            return ResponseEntity.ok(meetingService.updateMeeting(id, meeting));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Soft delete a meeting")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Meeting not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = meetingService.softDeleteMeeting(id);
        return deleted
            ? ResponseEntity.noContent().build()
            : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Search meetings by keyword")
    @ApiResponse(responseCode = "200", description = "Search results returned")
    @GetMapping("/search")
    public ResponseEntity<Page<BoardMeetingMinutes>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            meetingService.searchMeetings(q, PageRequest.of(page, size)));
    }

    // ─── EXPORT CSV ──────────────────────────────────────────────────────────

    @Operation(summary = "Export all meetings as CSV")
    @ApiResponse(responseCode = "200", description = "CSV file downloaded")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() {
        List<BoardMeetingMinutes> all = meetingService.getAllMeetingsForExport();

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Title,Meeting Date,Status,Created By,Created At\n");

        for (BoardMeetingMinutes m : all) {
            csv.append(m.getId()).append(",")
               .append(escapeCsv(m.getTitle())).append(",")
               .append(m.getMeetingDate()).append(",")
               .append(m.getStatus()).append(",")
               .append(escapeCsv(m.getCreatedBy())).append(",")
               .append(m.getCreatedAt()).append("\n");
        }

        byte[] bytes = csv.toString().getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "meetings.csv");
        headers.setContentLength(bytes.length);

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // ─── FILE UPLOAD ─────────────────────────────────────────────────────────

    @Operation(summary = "Upload a file attachment for a meeting (PDF or DOCX, max 5MB)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file type or size"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        // Size validation — max 5MB
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            return ResponseEntity.badRequest()
                .body("File too large. Maximum allowed size is 5MB.");
        }

        // Type validation — only PDF and DOCX
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null ||
            (!originalFilename.endsWith(".pdf") && !originalFilename.endsWith(".docx"))) {
            return ResponseEntity.badRequest()
                .body("Invalid file type. Only PDF and DOCX files are allowed.");
        }

        // Check meeting exists
        if (meetingService.getMeetingById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            String savedPath = meetingService.saveUploadedFile(id, file);
            return ResponseEntity.ok("File uploaded successfully: " + savedPath);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to save file: " + e.getMessage());
        }
    }
}