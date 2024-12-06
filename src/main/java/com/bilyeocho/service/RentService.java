package com.bilyeocho.service;

import com.bilyeocho.domain.Item;
import com.bilyeocho.domain.enums.ItemStatus;
import com.bilyeocho.domain.Rent;
import com.bilyeocho.domain.User;
import com.bilyeocho.dto.request.RentRequest;
import com.bilyeocho.dto.response.RentResponse;
import com.bilyeocho.error.CustomException;
import com.bilyeocho.error.ErrorCode;
import com.bilyeocho.repository.ItemRepository;
import com.bilyeocho.repository.RentRepository;
import com.bilyeocho.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentService {

    private final RentRepository rentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserAuthenticationService userAuthenticationService;

    @Transactional
    public RentResponse createRent(RentRequest rentRequest) {


        log.info(rentRequest.getRenterUserId());
        Item item = itemRepository.findById(Long.valueOf(rentRequest.getItemId()))
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));


        User renter = userRepository.findByUserId(rentRequest.getRenterUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (item.getStatus() != ItemStatus.AVAILABLE) {
            throw new CustomException(ErrorCode.ALREADY_RENTED);
        }

        Rent existingRent = rentRepository.findByItemAndUser(item, renter).orElse(null);

        if (existingRent != null) {
            existingRent.setStartTime(rentRequest.getStartTime());
            existingRent.setEndTime(rentRequest.getEndTime());

            item.setStatus(ItemStatus.RENTED);
            itemRepository.save(item);

            rentRepository.save(existingRent);

            return RentResponse.builder()
                    .rentId(existingRent.getId())
                    .itemId(item.getId().toString())
                    .renterUserId(renter.getUserId())
                    .startTime(existingRent.getStartTime())
                    .endTime(existingRent.getEndTime())
                    .rentStatus(item.getStatus())
                    .build();
        }

        Rent rent = Rent.builder()
                .item(item)
                .user(renter)
                .startTime(rentRequest.getStartTime())
                .endTime(rentRequest.getEndTime())
                .build();

        item.setStatus(ItemStatus.RENTED);
        itemRepository.save(item);

        Rent savedRent = rentRepository.save(rent);

        return RentResponse.builder()
                .rentId(savedRent.getId())
                .itemId(item.getId().toString())
                .renterUserId(renter.getUserId())
                .startTime(savedRent.getStartTime())
                .endTime(savedRent.getEndTime())
                .rentStatus(item.getStatus())
                .build();
    }



    @Transactional
    public RentResponse returnRent(Long rentId, String renterUserId) {
        Rent rent = rentRepository.findById(rentId)
                .orElseThrow(() -> new CustomException(ErrorCode.RENT_NOT_FOUND));

        if (!rent.getUser().getUserId().equals(renterUserId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_RENT_ACCESS);
        }

        rent.getItem().setStatus(ItemStatus.AVAILABLE);

        rentRepository.save(rent);
        itemRepository.save(rent.getItem());

        return RentResponse.builder()
                .rentId(rent.getId())
                .itemId(rent.getItem().getId().toString())
                .renterUserId(rent.getItem().getUser().getId().toString())
                .rentStatus(rent.getItem().getStatus())
                .build();
    }

    public List<RentResponse> getBorrowedItems(String userId) {
        List<Rent> rents = rentRepository.findByUserUserId(userId);
        return rents.stream()
                .map(rent -> RentResponse.builder()
                        .rentId(rent.getId())
                        .itemId(rent.getItem().getId().toString())
                        .renterUserId(rent.getUser().getUserId())
                        .startTime(rent.getStartTime())
                        .endTime(rent.getEndTime())
                        .rentStatus(rent.getItem().getStatus())
                        .build())
                .toList();
    }

    // 내가 빌려준 물품 조회
    public List<RentResponse> getLentItems(String userId) {

        List<Rent> rents = rentRepository.findByItemUserUserId(userId);
        return rents.stream()
                .map(rent -> RentResponse.builder()
                        .rentId(rent.getId())
                        .itemId(rent.getItem().getId().toString())
                        .renterUserId(rent.getUser().getUserId())
                        .startTime(rent.getStartTime())
                        .endTime(rent.getEndTime())
                        .rentStatus(rent.getItem().getStatus())
                        .build())
                .toList();
    }

    public RentResponse makeRentRequest(RentRequest rentRequest) {
        Item item = itemRepository.findById(Long.parseLong(rentRequest.getItemId()))
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        String userId = userAuthenticationService.getAuthenticatedUserId();

        User renter = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return RentResponse.builder()
                .renterUserId(renter.getId().toString())
                .build();
    }
}
