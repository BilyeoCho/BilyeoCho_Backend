package com.bilyeocho.dto;

import com.bilyeocho.domain.Category;
import com.bilyeocho.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ItemUpdateRequestDTO {
    private String itemName;
    private MultipartFile itemPhoto;
    private Category category;
    private Integer rentalDuration;
    private String itemDescription;

}
