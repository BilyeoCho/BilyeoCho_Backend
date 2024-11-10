package com.bilyeocho.service;

import com.bilyeocho.domain.Item;
import com.bilyeocho.domain.ItemStatus;
import com.bilyeocho.domain.Rent;
import com.bilyeocho.domain.User;
import com.bilyeocho.dto.request.RentRequest;
import com.bilyeocho.dto.response.RentResponse;
import com.bilyeocho.repository.ItemRepository;
import com.bilyeocho.repository.RentRepository;
import com.bilyeocho.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentService {

    private final RentRepository rentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public RentResponse createRent(RentRequest rentRequest) {
        Item item = itemRepository.findById(Long.parseLong(rentRequest.getItemId()))
                .orElseThrow(() -> new RuntimeException("물품을 찾을 수 없습니다."));

        User renter = userRepository.findById(Long.parseLong(rentRequest.getRenterId()))
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        if (item.getStatus() != ItemStatus.AVAILABLE) {
            throw new RuntimeException("물품이 대여 가능한 상태가 아닙니다.");
        }

        Rent rent = Rent.builder()
                .item(item)
                .startTime(rentRequest.getStartTime())
                .endTime(rentRequest.getEndTime())
                .build();

        item.setStatus(ItemStatus.RENTED);
        itemRepository.save(item);

        Rent savedRent = rentRepository.save(rent);

        return RentResponse.builder()
                .rentId(savedRent.getId())
                .itemId(item.getId().toString())
                .renterId(renter.getId().toString())
                .startTime(savedRent.getStartTime())
                .endTime(savedRent.getEndTime())
                .rentStatus(savedRent.getStatus())
                .build();
    }

    @Transactional
    public RentResponse returnRent(Long rentId) {
        Rent rent = rentRepository.findById(rentId)
                .orElseThrow(() -> new RuntimeException("대여 여부를 확인 할 수 없습니다."));

        rent.setStatus(ItemStatus.AVAILABLE);
        rent.getItem().setStatus(ItemStatus.AVAILABLE);

        rentRepository.save(rent);
        itemRepository.save(rent.getItem());

        return RentResponse.builder()
                .rentId(rent.getId())
                .itemId(rent.getItem().getId().toString())
                .renterId(rent.getItem().getUser().getId().toString())
                .startTime(rent.getStartTime())
                .endTime(rent.getEndTime())
                .rentStatus(rent.getStatus())
                .build();
    }

    // 예약 기능 추가 필요
}
