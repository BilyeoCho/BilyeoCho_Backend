package com.bilyeocho.dto;

import com.bilyeocho.domain.Category;
import com.bilyeocho.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemRegistRequestDTO {
    private String itemPhoto;

    private Category category;

    private String itemName;

    private String itemDescription;

    private int rentalDuration;

    private String userId;
}
