package com.bilyeocho.service;

import com.bilyeocho.domain.Category;
import com.bilyeocho.domain.Item;
import com.bilyeocho.domain.User;
import com.bilyeocho.dto.*;
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
    public ItemRegistResponseDTO registerItem(ItemRegistRequestDTO requestDTO, MultipartFile itemPhoto) {
        User user = userRepository.findByUserId(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        String itemPhotoUrl = s3Service.uploadFile(itemPhoto);

        Item newItem = Item.builder()
                .itemName(requestDTO.getItemName())
                .itemPhoto(itemPhotoUrl)
                .category(requestDTO.getCategory())
                .rentalDuration(requestDTO.getRentalDuration())
                .itemDescription(requestDTO.getItemDescription())
                .user(user)
                .build();
        Item savedItem = itemRepository.save(newItem);

        return new ItemRegistResponseDTO(savedItem.getItemId(), true);
    }


    @Override
    public ItemSearchResponseDTO getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 ID로 물품 조회가 불가능합니다"));
        return new ItemSearchResponseDTO(item);
    }

    @Override
    public List<ItemSearchResponseDTO> getAllItems() {
        List<Item> items = itemRepository.findAll();
        return items.stream()
                .map(ItemSearchResponseDTO::new)
                .toList();
    }

    @Override
    public ItemUpdateResponseDTO updateItem(Long id, ItemUpdateRequestDTO requestDTO) { // requestDTO로 수정
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
        if (requestDTO.getRentalDuration() != null) {
            item.setRentalDuration(requestDTO.getRentalDuration());
        }
        if (requestDTO.getItemDescription() != null) {
            item.setItemDescription(requestDTO.getItemDescription());
        }

        itemRepository.save(item);
        return new ItemUpdateResponseDTO(item);
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


    @Override
    public ItemSearchResponseDTO getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 ID로 물품 조회가 불가능합니다"));
        return new ItemSearchResponseDTO(item);
    }

    @Override
    public List<ItemSearchResponseDTO> getAllItems() {
        List<Item> items = itemRepository.findAll();
        return items.stream()
                .map(ItemSearchResponseDTO::new)
                .toList();
    }

    @Override
    public ItemUpdateResponseDTO updateItem(Long id, ItemUpdateRequestDTO requestDTO) { // requestDTO로 수정
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
        if (requestDTO.getRentalDuration() != null) {
            item.setRentalDuration(requestDTO.getRentalDuration());
        }
        if (requestDTO.getItemDescription() != null) {
            item.setItemDescription(requestDTO.getItemDescription());
        }

        itemRepository.save(item);
        return new ItemUpdateResponseDTO(item);
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
