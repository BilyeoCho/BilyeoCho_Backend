package com.bilyeocho.dto.request;

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

    public RentRequest(String itemId, String renterId) {
        this.itemId = itemId;
        this.renterId = renterId;
    }
}
