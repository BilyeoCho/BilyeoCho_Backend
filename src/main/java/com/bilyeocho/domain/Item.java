package com.bilyeocho.domain;

import jakarta.persistence.*;
import com.bilyeocho.domain.User;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", nullable = false) //pk
    private Long itemId;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "item_photo", nullable = false)
    private String itemPhoto;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Column(name = "rental_duration", nullable = false)
    private Integer rentalDuration;

    @Column(name = "item_description", length = 1000, nullable = false) // 필요시 길이 조절
    private String itemDescription;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}