package com.bilyeocho.service;

import com.bilyeocho.domain.Item;
import com.bilyeocho.domain.User;
import com.bilyeocho.dto.ItemRegistRequestDTO;
import com.bilyeocho.dto.ItemRegistResponseDTO;
import com.bilyeocho.repository.ItemRepository;
import com.bilyeocho.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
                .orElseThrow(() -> new RuntimeException("유저 조회가 불가능합니다"));

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
}
