package com.bilyeocho.service;

import com.bilyeocho.domain.Item;
import com.bilyeocho.domain.enums.ItemStatus;
import com.bilyeocho.domain.enums.UserRole;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
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
        // 인증된 사용자 ID 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // username 또는 userId로 설정됨

        log.info("userId: " + userId);
        // 사용자 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 사진 업로드
        String itemPhotoUrl = s3Service.uploadFile(itemPhoto);

        // 물품 등록
        Item newItem = Item.builder()
                .itemName(requestDTO.getItemName())
                .itemPhoto(itemPhotoUrl)
                .itemCategory(requestDTO.getItemCategory())
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
        // 물품 조회
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        return new ItemSearchResponse(item);
    }

    @Override
    public List<ItemSearchResponse> getAllItems() {
        // 전체 물품 조회
        List<Item> items = itemRepository.findAll();
        return items.stream()
                .map(ItemSearchResponse::new)
                .toList();
    }

    @Override
    public ItemUpdateResponse updateItem(Long id, ItemUpdateRequest requestDTO, String userId) {
        // 물품 조회
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // 물품 등록자 확인
        if (!item.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Forbidden: Not allowed to update this item");
        }

        // 업데이트 로직
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
        if (requestDTO.getItemCategory() != null) {
            item.setItemCategory(requestDTO.getItemCategory());
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
        // 물품 조회
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // 사용자 확인
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 관리자 또는 등록자인지 확인
        if (!user.getRole().equals(UserRole.ADMIN) && !item.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Forbidden: Not allowed to delete this item");
        }

        // 관련 데이터 삭제
        rentRepository.deleteByItem(item);

        // 사진 삭제
        if (item.getItemPhoto() != null) {
            s3Service.deleteFile(item.getItemPhoto());
        }

        // 물품 삭제
        itemRepository.delete(item);
    }

    @Override
    public List<ItemSearchResponse> getLatestItems() {
        List<Item> latestItems = itemRepository.findTop4ByOrderByIdDesc();
        return latestItems.stream()
                .map(ItemSearchResponse::new)
                .toList();
    }

    @Override
    public List<ItemSearchResponse> getItemsByUserId(String userId) {
        List<Item> items = itemRepository.findByUserUserId(userId);
        return items.stream()
                .map(ItemSearchResponse::new)
                .toList();
    }
}