package com.bilyeocho.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Operation(summary = "테스트 페이지", description = "API 설명.")
    @GetMapping("/")
    public String test() {
        return "Hello World~";
    }
}
