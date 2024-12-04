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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final RedisTemplate<String, String> redisTemplate;
    private final RentRepository rentRepository;
    private final UserAuthenticationService userAuthenticationService;
    private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);

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
        String authenticatedUserId = userAuthenticationService.getAuthenticatedUserId();

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (item.getUser() == null || !item.getUser().getUserId().equals(authenticatedUserId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (requestDTO.getItemName() != null) {
            item.setItemName(requestDTO.getItemName());
        }

        if (requestDTO.getItemPhoto() != null) {
            if (!requestDTO.getItemPhoto().isEmpty()) {
                if (item.getItemPhoto() != null) {
                    s3Service.deleteFile(item.getItemPhoto());
                }
                String newPhotoUrl = s3Service.uploadFile(requestDTO.getItemPhoto());
                item.setItemPhoto(newPhotoUrl);
            }
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