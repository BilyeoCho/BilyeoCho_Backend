package com.bilyeocho.service;

import com.bilyeocho.domain.Item;
import com.bilyeocho.domain.User;
import com.bilyeocho.domain.enums.ItemStatus;
import com.bilyeocho.domain.enums.UserRole;
import com.bilyeocho.dto.request.ItemRegistRequest;
import com.bilyeocho.dto.request.ItemUpdateRequest;
import com.bilyeocho.dto.response.ItemRegistResponse;
import com.bilyeocho.dto.response.ItemSearchResponse;
import com.bilyeocho.dto.response.ItemUpdateResponse;
import com.bilyeocho.error.CustomException;
import com.bilyeocho.error.ErrorCode;
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
    private final UserAuthenticationService userAuthenticationService;

    @Override
    @Transactional
    public ItemRegistResponse registerItem(ItemRegistRequest requestDTO, MultipartFile itemPhoto) {
        String userId = userAuthenticationService.getAuthenticatedUserId();

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String itemPhotoUrl = s3Service.uploadFile(itemPhoto);

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
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));
        if (item.getUser() == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        return new ItemSearchResponse(item);
    }

    @Override
    public List<ItemSearchResponse> getAllItems() {
        List<Item> items = itemRepository.findAll();
        return items.stream()
                .peek(item -> {
                    if (item.getUser() == null) {
                        throw new CustomException(ErrorCode.USER_NOT_FOUND);
                    }
                })
                .map(ItemSearchResponse::new)
                .toList();
    }

    @Override
    public ItemUpdateResponse updateItem(Long id, ItemUpdateRequest requestDTO, String userId) {
        // 인증된 사용자 ID 가져오기
        String authenticatedUserId = userAuthenticationService.getAuthenticatedUserId();

        // 물품 조회
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        // 권한 확인
        if (item.getUser() == null || !item.getUser().getUserId().equals(authenticatedUserId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 물품 이름 업데이트
        if (requestDTO.getItemName() != null) {
            item.setItemName(requestDTO.getItemName());
        }

        // 물품 사진 업데이트
        if (requestDTO.getItemPhoto() == null) {
            System.out.println("itemPhoto is null.");
        } else {
            System.out.println("itemPhoto is received. Name: " + requestDTO.getItemPhoto().getOriginalFilename()
                    + ", Size: " + requestDTO.getItemPhoto().getSize());

            if (!requestDTO.getItemPhoto().isEmpty()) {
                // 기존 사진이 있으면 삭제
                if (item.getItemPhoto() != null) {
                    s3Service.deleteFile(item.getItemPhoto());
                }
                // 새로운 사진 업로드
                String newPhotoUrl = s3Service.uploadFile(requestDTO.getItemPhoto());
                item.setItemPhoto(newPhotoUrl);
            } else {
                System.out.println("itemPhoto is empty. Keeping the existing photo.");
            }
        }

        // 카테고리 업데이트
        if (requestDTO.getItemCategory() != null) {
            item.setItemCategory(requestDTO.getItemCategory());
        }

        // 상세 설명 업데이트
        if (requestDTO.getItemDescription() != null) {
            item.setItemDescription(requestDTO.getItemDescription());
        }

        // 가격 업데이트
        if (requestDTO.getPrice() != null) {
            item.setPrice(requestDTO.getPrice());
        }

        // 상태 업데이트
        if (requestDTO.getStatus() != null) {
            item.setStatus(requestDTO.getStatus());
        }

        // 변경된 내용 저장
        itemRepository.save(item);

        // 업데이트된 내용으로 응답 반환
        return new ItemUpdateResponse(item);
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId, String userId) {
        String authenticatedUserId = userAuthenticationService.getAuthenticatedUserId();

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (item.getUser() == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        if (!item.getUser().getUserId().equals(authenticatedUserId)) {
            User user = userRepository.findByUserId(authenticatedUserId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            if (!user.getRole().equals(UserRole.ADMIN)) {
                throw new CustomException(ErrorCode.FORBIDDEN_ADMIN_ACCESS);
            }
        }

        rentRepository.deleteByItem(item);

        if (item.getItemPhoto() != null) {
            s3Service.deleteFile(item.getItemPhoto());
        }

        itemRepository.delete(item);
    }

    @Override
    public List<ItemSearchResponse> getLatestItems() {
        List<Item> latestItems = itemRepository.findTop4ByOrderByIdDesc();
        return latestItems.stream()
                .peek(item -> {
                    if (item.getUser() == null) {
                        throw new CustomException(ErrorCode.USER_NOT_FOUND);
                    }
                })
                .map(ItemSearchResponse::new)
                .toList();
    }

    @Override
    public List<ItemSearchResponse> getItemsByUserId(String userId) {
        List<Item> items = itemRepository.findByUserUserId(userId);
        return items.stream()
                .peek(item -> {
                    if (item.getUser() == null) {
                        throw new CustomException(ErrorCode.USER_NOT_FOUND);
                    }
                })
                .map(ItemSearchResponse::new)
                .toList();
    }
}