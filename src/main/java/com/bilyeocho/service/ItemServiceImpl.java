package com.bilyeocho.service;

import com.bilyeocho.domain.Item;
import com.bilyeocho.domain.User;
import com.bilyeocho.dto.ItemRegistRequestDTO;
import com.bilyeocho.dto.ItemRegistResponseDTO;
import com.bilyeocho.repository.ItemRepository;
import com.bilyeocho.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ItemRegistResponseDTO regsiterItem(ItemRegistRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("유저 조회가 불가능합니다"));

        Item newItem = Item.builder()
                .itemName(requestDTO.getItemName())
                .itemPhoto(requestDTO.getItemPhoto())
                .category(requestDTO.getCategory())
                .startTime(requestDTO.getStartTime())
                .user(user)
                .build();

        Item savedItem = itemRepository.save(newItem);

        return new ItemRegistResponseDTO(savedItem.getItemId(), true);
    }

}
