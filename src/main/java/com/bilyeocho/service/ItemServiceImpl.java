package com.bilyeocho.service;

import com.bilyeocho.domain.Item;
import com.bilyeocho.domain.ItemStatus;
import com.bilyeocho.domain.Role;
import com.bilyeocho.domain.User;
import com.bilyeocho.dto.request.ItemRegistRequest;
import com.bilyeocho.dto.request.ItemUpdateRequest;
import com.bilyeocho.dto.response.ItemRegistResponse;
import com.bilyeocho.dto.response.ItemSearchResponse;
import com.bilyeocho.dto.response.ItemUpdateResponse;
import com.bilyeocho.repository.ItemRepository;
import com.bilyeocho.repository.RentRepository;
import com.bilyeocho.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final RentRepository rentRepository;

    @Override
    @Transactional
    public ItemRegistResponse registerItem(ItemRegistRequest requestDTO, MultipartFile itemPhoto) {
        User user = userRepository.findByUserId(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        String itemPhotoUrl = s3Service.uploadFile(itemPhoto);

        Item newItem = Item.builder()
                .itemName(requestDTO.getItemName())
                .itemPhoto(itemPhotoUrl)
                .category(requestDTO.getCategory())
                .itemDescription(requestDTO.getItemDescription())
                .status(ItemStatus.AVAILABLE)
                .price(requestDTO.getPrice())
                .user(user)
                .build();
        Item savedItem = itemRepository.save(newItem);

        return new ItemRegistResponse(savedItem.getId(), true);
    }


    @Override
    public ItemSearchResponse getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 ID로 물품 조회가 불가능합니다"));
        return new ItemSearchResponse(item);
    }

    @Override
    public List<ItemSearchResponse> getAllItems() {
        List<Item> items = itemRepository.findAll();
        return items.stream()
                .map(ItemSearchResponse::new)
                .toList();
    }

    @Override
    public ItemUpdateResponse updateItem(Long id, ItemUpdateRequest requestDTO, String userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 ID로 물품 조회가 불가능합니다"));

        // 물품 등록자와 요청한 사용자의 ID가 일치하는지 확인
        if (!item.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to perform this action");
        }

        if (requestDTO.getItemName() != null) {
            item.setItemName(requestDTO.getItemName());
        }
        if (requestDTO.getItemPhoto() != null && !requestDTO.getItemPhoto().isEmpty()) {
            if (item.getItemPhoto() != null) {
                s3Service.deleteFile(item.getItemPhoto());
            }
            String newPhotoUrl = s3Service.uploadFile(requestDTO.getItemPhoto());
            item.setItemPhoto(newPhotoUrl);
        }
        if (requestDTO.getCategory() != null) {
            item.setCategory(requestDTO.getCategory());
        }
        if (requestDTO.getItemDescription() != null) {
            item.setItemDescription(requestDTO.getItemDescription());
        }
        if (requestDTO.getPrice() != null) {
            item.setPrice(requestDTO.getPrice());
        }
        if (requestDTO.getStatus() != null) {
            item.setStatus(requestDTO.getStatus());
        }

        itemRepository.save(item);
        return new ItemUpdateResponse(item);
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId, String userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("해당 ID로 물품 조회가 불가능합니다"));

        // 요청을 보낸 사용자 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 관리자 계정인지 확인
        // 관리자 계정인지 확인
        if (!user.getRole().equals(Role.ADMIN) && !item.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to perform this action");
        }

        // rents 테이블의 관련 데이터 삭제
        rentRepository.deleteByItem(item);

        // S3의 이미지 삭제
        if (item.getItemPhoto() != null) {
            s3Service.deleteFile(item.getItemPhoto());
        }

        // item 삭제
        itemRepository.delete(item);
    }

    @Override
    public List<ItemSearchResponse> getLatestItems() {
        List<Item> latestItems = itemRepository.findTop4ByOrderByIdDesc();
        return latestItems.stream()
                .map(ItemSearchResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemSearchResponse> getItemsByUserId(String userId) {
        List<Item> items = itemRepository.findByUserUserId(userId);
        return items.stream()
                .map(ItemSearchResponse::new)
                .toList();
    }
}