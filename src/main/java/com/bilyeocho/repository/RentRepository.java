package com.bilyeocho.repository;

import com.bilyeocho.domain.Item;
import com.bilyeocho.domain.Rent;
import com.bilyeocho.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RentRepository extends JpaRepository<Rent, Long> {

    Optional<Rent> findByItemAndUser(Item item, User user);
}