package com.bilyeocho.controller;


import com.bilyeocho.dto.ItemRegistRequestDTO;
import com.bilyeocho.dto.ItemRegistResponseDTO;
import com.bilyeocho.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


    //물품 등록 API
    @Operation(summary = "물품 등록", description = "사용자가 물품을 등록합니다.", security = {@SecurityRequirement(name = "bearerAuth")})
    @PostMapping("/regist")
    public ResponseEntity<ItemRegistResponseDTO> registerItem(@RequestBody ItemRegistRequestDTO requestDTO) {
        ItemRegistResponseDTO responseDTO = itemService.registerItem(requestDTO);

        return ResponseEntity.ok(responseDTO);
    }

}
