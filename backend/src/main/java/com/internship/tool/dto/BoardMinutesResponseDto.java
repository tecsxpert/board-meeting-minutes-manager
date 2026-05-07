// BoardMinutesResponseDto.java: Response DTO returned to clients for BoardMinutes records.
// Includes all fields: audit timestamps, AI fields, and status as string for serialization clarity.

package com.internship.tool.dto;

import com.internship.tool.entity.BoardMinutes;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardMinutesResponseDto {

    private UUID id;
    private String title;
    private LocalDate meetingDate;
    private String attendees;
    private String content;
    private String status;
    private String aiDescription;
    private String aiRecommendations;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Static factory to convert a BoardMinutes entity to a response DTO.
     */
    public static BoardMinutesResponseDto from(BoardMinutes entity) {
        return BoardMinutesResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .meetingDate(entity.getMeetingDate())
                .attendees(entity.getAttendees())
                .content(entity.getContent())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .aiDescription(entity.getAiDescription())
                .aiRecommendations(entity.getAiRecommendations())
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
