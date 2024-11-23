package com.bilyeocho.domain;

import com.bilyeocho.domain.enums.ItemCategory;
import com.bilyeocho.domain.enums.ItemStatus;
import jakarta.persistence.*;
import lombok.*;

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
    private ItemCategory itemCategory;

    @Column(name = "item_description", length = 1000, nullable = false) // 필요시 길이 조절
    private String itemDescription;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_status", nullable = false)
    private ItemStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;




}