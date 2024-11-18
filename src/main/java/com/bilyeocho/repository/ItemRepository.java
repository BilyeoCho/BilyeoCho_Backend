package com.bilyeocho.repository;

import com.bilyeocho.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findTop4ByOrderByIdDesc();
    List<Item> findByUserUserId(String userId);
}
