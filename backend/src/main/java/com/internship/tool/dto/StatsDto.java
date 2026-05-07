// StatsDto.java: DTO for returning KPI statistics about board minutes by status count.
// Used by the /api/minutes/stats endpoint to power dashboard KPI cards.

package com.internship.tool.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsDto {

    private long total;
    private long published;
    private long draft;
    private long archived;
}
