package com.bilyeocho.service;

import com.bilyeocho.domain.Item;
import com.bilyeocho.domain.enums.ItemStatus;
import com.bilyeocho.domain.Rent;
import com.bilyeocho.domain.User;
import com.bilyeocho.dto.request.RentRequest;
import com.bilyeocho.dto.response.RentResponse;
import com.bilyeocho.repository.ItemRepository;
import com.bilyeocho.repository.RentRepository;
import com.bilyeocho.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentService {

    private final RentRepository rentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public RentResponse createRent(RentRequest rentRequest) {
        try {
            Item item = itemRepository.findById(Long.parseLong(rentRequest.getItemId()))
                    .orElseThrow(() -> {
                        log.error("대여 실패: Item ID {}를 찾을 수 없습니다.", rentRequest.getItemId());
                        return new RuntimeException("Item not found with ID: " + rentRequest.getItemId());
                    });

            User renter = userRepository.findById(Long.parseLong(rentRequest.getRenterId()))
                    .orElseThrow(() -> {
                        log.error("대여 실패: User ID {}를 찾을 수 없습니다.", rentRequest.getRenterId());
                        return new RuntimeException("User not found with ID: " + rentRequest.getRenterId());
                    });

            if (item.getStatus() != ItemStatus.AVAILABLE) {
                log.error("대여 실패: Item ID {}는 이미 대여 중입니다.", rentRequest.getItemId());
                throw new RuntimeException("Item is already rented: " + rentRequest.getItemId());
            }

            Rent existingRent = rentRepository.findByItemAndUser(item, renter).orElse(null);

            if (existingRent != null) {
                log.info("기존 대여 정보 갱신: Rent ID {}", existingRent.getId());
                existingRent.setStartTime(rentRequest.getStartTime());
                existingRent.setEndTime(rentRequest.getEndTime());
                item.setStatus(ItemStatus.RENTED);

                itemRepository.save(item);
                rentRepository.save(existingRent);

                return RentResponse.builder()
                        .rentId(existingRent.getId())
                        .itemId(item.getId().toString())
                        .renterId(renter.getId().toString())
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

            log.info("새로운 대여 생성: Rent ID {}, Item ID {}, Renter ID {}", savedRent.getId(), item.getId(), renter.getId());

            return RentResponse.builder()
                    .rentId(savedRent.getId())
                    .itemId(item.getId().toString())
                    .renterId(renter.getId().toString())
                    .startTime(savedRent.getStartTime())
                    .endTime(savedRent.getEndTime())
                    .rentStatus(item.getStatus())
                    .build();
        } catch (RuntimeException e) {
            log.error("대여 생성 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public RentResponse returnRent(Long rentId, Long renterId) {
        try {
            Rent rent = rentRepository.findById(rentId)
                    .orElseThrow(() -> {
                        log.error("반납 실패: Rent ID {}를 찾을 수 없습니다.", rentId);
                        return new RuntimeException("Rent not found with ID: " + rentId);
                    });

            if (!rent.getUser().getId().equals(renterId)) {
                log.error("반납 실패: Renter ID {}는 Rent ID {}에 접근 권한이 없습니다.", renterId, rentId);
                throw new RuntimeException("Access denied for Rent ID: " + rentId);
            }

            rent.getItem().setStatus(ItemStatus.AVAILABLE);

            rentRepository.save(rent);
            itemRepository.save(rent.getItem());

            log.info("대여 반납 성공: Rent ID {}, Item ID {}", rent.getId(), rent.getItem().getId());

            return RentResponse.builder()
                    .rentId(rent.getId())
                    .itemId(rent.getItem().getId().toString())
                    .renterId(rent.getUser().getId().toString())
                    .rentStatus(rent.getItem().getStatus())
                    .build();
        } catch (RuntimeException e) {
            log.error("대여 반납 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<RentResponse> getBorrowedItems(String userId) {
        try {
            List<Rent> rents = rentRepository.findByUserUserId(userId);
            log.info("내가 빌린 물품 조회: User ID {}, {}건 반환", userId, rents.size());
            return rents.stream()
                    .map(rent -> RentResponse.builder()
                            .rentId(rent.getId())
                            .itemId(rent.getItem().getId().toString())
                            .renterId(rent.getUser().getUserId())
                            .startTime(rent.getStartTime())
                            .endTime(rent.getEndTime())
                            .rentStatus(rent.getItem().getStatus())
                            .build())
                    .toList();
        } catch (RuntimeException e) {
            log.error("내가 빌린 물품 조회 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<RentResponse> getLentItems(String userId) {
        try {
            List<Rent> rents = rentRepository.findByItemUserUserId(userId);
            log.info("내가 빌려준 물품 조회: User ID {}, {}건 반환", userId, rents.size());
            return rents.stream()
                    .map(rent -> RentResponse.builder()
                            .rentId(rent.getId())
                            .itemId(rent.getItem().getId().toString())
                            .renterId(rent.getUser().getUserId())
                            .startTime(rent.getStartTime())
                            .endTime(rent.getEndTime())
                            .rentStatus(rent.getItem().getStatus())
                            .build())
                    .toList();
        } catch (RuntimeException e) {
            log.error("내가 빌려준 물품 조회 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
}