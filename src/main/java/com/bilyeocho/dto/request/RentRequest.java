package com.bilyeocho.dto.request;

import com.bilyeocho.domain.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentRequest {
    private String itemId;
    private String renterId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
