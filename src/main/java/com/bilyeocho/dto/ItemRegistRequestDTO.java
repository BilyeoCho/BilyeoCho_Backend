package com.bilyeocho.dto;

import com.bilyeocho.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Setter
public class ItemRegistRequestDTO {
    private String itemId;

    private String itemName;

    private String itemPhoto;

    private String category;

    private Long userId;
}


