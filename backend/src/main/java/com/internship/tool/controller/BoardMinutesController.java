// BoardMinutesController.java: REST controller exposing /api/minutes endpoints.
// All endpoints require JWT authentication. Swagger @Operation and @ApiResponse annotations are included.

package com.internship.tool.controller;

import com.internship.tool.dto.BoardMinutesDto;
import com.internship.tool.dto.BoardMinutesResponseDto;
import com.internship.tool.dto.StatsDto;
import com.internship.tool.service.BoardMinutesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/minutes")
@RequiredArgsConstructor
@Tag(name = "Board Minutes", description = "Board Meeting Minutes CRUD, Search, Stats, Export and Upload")
@SecurityRequirement(name = "Bearer Auth")
public class BoardMinutesController {

    private final BoardMinutesService service;

    // ─── GET /api/minutes ────────────────────────────────────────────────────

    @Operation(summary = "Get all board minutes (paginated)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paginated list returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized — JWT missing or invalid")
    })
    @GetMapping
    public ResponseEntity<Page<BoardMinutesResponseDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getAll(PageRequest.of(page, size)));
    }

    // ─── GET /api/minutes/stats ───────────────────────────────────────────────

    @Operation(summary = "Get KPI statistics by status")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stats returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/stats")
    public ResponseEntity<StatsDto> getStats() {
        return ResponseEntity.ok(service.getStats());
    }

    // ─── GET /api/minutes/search ─────────────────────────────────────────────

    @Operation(summary = "Search board minutes by keyword in title or content")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search results returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<BoardMinutesResponseDto>> search(
            @RequestParam("q") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.search(keyword, PageRequest.of(page, size)));
    }

    // ─── GET /api/minutes/export ─────────────────────────────────────────────

    @Operation(summary = "Export all board minutes as a CSV file")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "CSV file streamed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/export")
    public void exportCsv(HttpServletResponse response) throws IOException {
        service.exportCsv(response);
    }

    // ─── GET /api/minutes/{id} ───────────────────────────────────────────────

    @Operation(summary = "Get a single board minutes record by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Record found"),
        @ApiResponse(responseCode = "404", description = "Record not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BoardMinutesResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // ─── POST /api/minutes ───────────────────────────────────────────────────

    @Operation(summary = "Create a new board minutes record")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Record created"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<BoardMinutesResponseDto> create(@Valid @RequestBody BoardMinutesDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    // ─── PUT /api/minutes/{id} ───────────────────────────────────────────────

    @Operation(summary = "Update an existing board minutes record")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Record updated"),
        @ApiResponse(responseCode = "404", description = "Record not found"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BoardMinutesResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody BoardMinutesDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // ─── DELETE /api/minutes/{id} ────────────────────────────────────────────

    @Operation(summary = "Soft-delete a board minutes record")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Record soft-deleted"),
        @ApiResponse(responseCode = "404", description = "Record not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    // ─── POST /api/minutes/upload ────────────────────────────────────────────

    @Operation(summary = "Upload a PDF or DOCX file attachment (max 5MB)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file type or exceeds size limit"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file) throws IOException {
        String filename = service.uploadFile(file);
        return ResponseEntity.ok("File uploaded successfully: " + filename);
    }
}
