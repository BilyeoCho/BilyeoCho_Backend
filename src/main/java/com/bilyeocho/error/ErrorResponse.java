package com.bilyeocho.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Schema(description = "오류 응답")
@Getter
@AllArgsConstructor
public class ErrorResponse {

    @Schema(description = "HTTP 상태 코드")
    private HttpStatus status;
    @Schema(description = "오류 메시지")
    private String message;
}
