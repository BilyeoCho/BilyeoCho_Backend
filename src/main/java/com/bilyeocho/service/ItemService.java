package com.bilyeocho.service;

import com.bilyeocho.dto.ItemRegistRequestDTO;
import com.bilyeocho.dto.ItemRegistResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ItemService {

    ItemRegistResponseDTO registerItem(ItemRegistRequestDTO requestDTO, MultipartFile itemPhoto);

}
