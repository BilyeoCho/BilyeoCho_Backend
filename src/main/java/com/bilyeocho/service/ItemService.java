package com.bilyeocho.service;

import com.bilyeocho.dto.request.ItemRegistRequest;
import com.bilyeocho.dto.request.ItemUpdateRequest;
import com.bilyeocho.dto.response.ItemRegistResponse;
import com.bilyeocho.dto.response.ItemSearchResponse;
import com.bilyeocho.dto.response.ItemUpdateResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {

    //물품 등록
    ItemRegistResponse registerItem(ItemRegistRequest requestDTO, MultipartFile itemPhoto);

    //물품 검색
    ItemSearchResponse getItemById(Long id);
    List<ItemSearchResponse> getAllItems();

    //물품 업데이트
    ItemUpdateResponse updateItem(Long id, ItemUpdateRequest requestDTO, String userId);

    //물품 삭제
    public void deleteItem(Long itemId, String userId);

    //최신 물품 4개 불러오기
    public List<ItemSearchResponse> getLatestItems();

    //사용자별 등록 물품 조회
    List<ItemSearchResponse> getItemsByUserId(String userId);
}
