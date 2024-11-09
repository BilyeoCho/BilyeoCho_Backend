package com.bilyeocho.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ResponseDto<T> {
    private int status;  // HTTP 상태 코드
    private String message;  // 응답 메시지
    private T data;  // 응답 데이터

    // 성공적인 응답을 생성하는 정적 메서드
    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>(HttpStatus.OK.value(), "성공", data);
    }

    // 오류 메시지를 생성하는 정적 메서드
    public static ResponseDto<String> create(String message) {
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), message, null);
    }
}
