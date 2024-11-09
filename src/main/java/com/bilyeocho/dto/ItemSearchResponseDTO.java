package com.bilyeocho.dto;

import com.bilyeocho.domain.Category;
import com.bilyeocho.domain.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemSearchResponseDTO {
    private Long itemId;
    private String itemName;
    private String itemDescription;
    private Category category;
    private String itemPhoto;
    private Integer rentalDuration;
    private String userId;

    public ItemSearchResponseDTO(Item item) {
        this.itemId = item.getId();
        this.itemName = item.getItemName();
        this.itemDescription = item.getItemDescription();
        this.category = item.getCategory();
        this.itemPhoto = item.getItemPhoto();
        this.rentalDuration = item.getRentalDuration();
        this.userId = item.getUser().getUserId();
    }
}
