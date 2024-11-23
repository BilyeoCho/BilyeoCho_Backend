package com.bilyeocho.dto.request;

import com.bilyeocho.domain.enums.ItemCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchRequest {
    private Long itemId;
    private String itemName;
    private ItemCategory itemCategory;
}
