package com.bilyeocho.dto;

import com.bilyeocho.domain.Category;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ItemRegistRequestDTO {
    private MultipartFile itemPhoto;

    private Category category;

    private String itemName;

    private String itemDescription;

    private int rentalDuration;

    private String userId;
}

