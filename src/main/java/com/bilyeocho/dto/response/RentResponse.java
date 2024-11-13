package com.bilyeocho.dto.response;

import com.bilyeocho.domain.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RentResponse {
    private Long rentId;
    private String itemId;
    private String renterId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ItemStatus rentStatus;
}
