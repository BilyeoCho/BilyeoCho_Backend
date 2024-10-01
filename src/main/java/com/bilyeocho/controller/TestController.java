package com.bilyeocho.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Operation(summary = "테스트 엔드포인트", description = "기본 테스트를 위한 엔드포인트입니다.")
    @GetMapping("/")
    public String test() {
        return "test";
    }
}
