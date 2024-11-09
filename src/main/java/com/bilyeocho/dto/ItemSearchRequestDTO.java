package com.bilyeocho.dto;

import com.bilyeocho.domain.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchRequestDTO {
    private Long itemId;
    private String itemName;
    private Category category;
}
