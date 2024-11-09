package com.bilyeocho.dto.request;

import com.bilyeocho.domain.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchRequest {
    private Long itemId;
    private String itemName;
    private Category category;
}
