// BoardMinutesDto.java: Request DTO for creating or updating a BoardMinutes record.
// Includes Bean Validation annotations for title (NotBlank) and meeting_date (NotNull).

package com.internship.tool.dto;

import com.internship.tool.entity.BoardMinutes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardMinutesDto {

    @NotBlank(message = "Title must not be blank")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotNull(message = "Meeting date must not be null")
    private LocalDate meetingDate;

    private String attendees;

    private String content;

    private BoardMinutes.Status status;

    private String aiDescription;

    private String aiRecommendations;
}
