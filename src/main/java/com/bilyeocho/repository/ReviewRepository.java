package com.bilyeocho.repository;

import com.bilyeocho.domain.Item;
import com.bilyeocho.domain.Review;
import com.bilyeocho.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByUser(User user);
    List<Review> findByItem(Item item);
    void deleteByUser(User user);
}
