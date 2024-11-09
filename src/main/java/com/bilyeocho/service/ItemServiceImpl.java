package com.bilyeocho.service;

import com.bilyeocho.domain.Item;
import com.bilyeocho.domain.ItemStatus;
import com.bilyeocho.domain.User;
import com.bilyeocho.dto.request.ItemRegistRequest;
import com.bilyeocho.dto.request.ItemUpdateRequest;
import com.bilyeocho.dto.response.ItemRegistResponse;
import com.bilyeocho.dto.response.ItemSearchResponse;
import com.bilyeocho.dto.response.ItemUpdateResponse;
import com.bilyeocho.repository.ItemRepository;
import com.bilyeocho.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    public ItemUpdateResponse updateItem(Long id, ItemUpdateRequest requestDTO) { // requestDTO로 수정
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 ID로 물품 조회가 불가능합니다"));

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

        if (requestDTO.getStatus() != null){
            item.setStatus(requestDTO.getStatus());
        }




        itemRepository.save(item);
        return new ItemUpdateResponse(item);
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 ID로 물품 조회가 불가능합니다"));

        if (item.getItemPhoto() != null) {
            s3Service.deleteFile(item.getItemPhoto());
        }

        itemRepository.delete(item);
    }
}