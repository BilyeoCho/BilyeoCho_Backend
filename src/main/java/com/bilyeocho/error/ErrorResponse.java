package com.bilyeocho.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private HttpStatus status;
    private String message;
}
