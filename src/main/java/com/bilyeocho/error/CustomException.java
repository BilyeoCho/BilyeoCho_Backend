package com.bilyeocho.error;

import lombok.Data;

@Data
public class CustomException extends RuntimeException{

    private final ErrorCode errorCode;
}
