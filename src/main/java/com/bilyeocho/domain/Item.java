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
    private Long id;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "item_photo", nullable = false)
    private String itemPhoto;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Column(name = "item_description", length = 1000, nullable = false) // 필요시 길이 조절
    private String itemDescription;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_status", nullable = false)
    private ItemStatus status;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "user_id", nullable = false)
    private User user;




}