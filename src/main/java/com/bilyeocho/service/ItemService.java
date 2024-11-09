package com.bilyeocho.service;

import com.bilyeocho.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {

    //물품 등록
    ItemRegistResponseDTO registerItem(ItemRegistRequestDTO requestDTO, MultipartFile itemPhoto);

    //물품 검색
    ItemSearchResponseDTO getItemById(Long id);
    List<ItemSearchResponseDTO> getAllItems();

    //물품 업데이트
    ItemUpdateResponseDTO updateItem(Long id, ItemUpdateRequestDTO requestDTO);

    //물품 삭제
    public void deleteItem(Long id);
}
