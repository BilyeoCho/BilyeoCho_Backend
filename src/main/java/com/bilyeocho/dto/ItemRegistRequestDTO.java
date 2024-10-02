package com.bilyeocho.dto;

import com.bilyeocho.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemRegistRequestDTO {
    private String itemName;

    private String itemPhoto;

    private String category;

    private Long userId;

    private LocalDateTime startTime;

}


