package com.bilyeocho.dto.response;

import com.bilyeocho.domain.enums.ItemCategory;
import com.bilyeocho.domain.Item;
import com.bilyeocho.domain.enums.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemSearchResponse {
    private Long itemId;
    private String itemName;
    private String itemDescription;
    private ItemCategory itemCategory;
    private String itemPhoto;
    private Integer price;
    private ItemStatus status;
    private String userId;
    private Long viewCount;

    public ItemSearchResponse(Item item) {
        this.itemId = item.getId();
        this.itemName = item.getItemName();
        this.itemDescription = item.getItemDescription();
        this.itemCategory = item.getItemCategory();
        this.itemPhoto = item.getItemPhoto();
        this.price = item.getPrice();
        this.status = item.getStatus();
        this.userId = item.getUser().getUserId();
    }
}
