package com.bilyeocho.repository;

import com.bilyeocho.domain.Item;
import com.bilyeocho.domain.Review;
import com.bilyeocho.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByUser(User user);
    List<Review> findByItem(Item item);

    @Modifying
    @Query("DELETE FROM Review r WHERE r.item.user = :user")
    void deleteByItemUser(@Param("user") User user);

    void deleteByUser(User user);
}
