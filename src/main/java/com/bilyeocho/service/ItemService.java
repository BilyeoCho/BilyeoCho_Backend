package com.bilyeocho.service;

import com.bilyeocho.dto.ItemRegistRequestDTO;
import com.bilyeocho.dto.ItemRegistResponseDTO;

public interface ItemService {

    ItemRegistResponseDTO registerItem(ItemRegistRequestDTO requestDTO);

}