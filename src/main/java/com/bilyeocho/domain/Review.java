package com.bilyeocho.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double rate;

    private String reviewTitle;

    private String reviewPhoto;

    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")  // 외래 키 설정 (user_id)
    private User user;

    // Item과의 관계 설정: 하나의 아이템에 대해 여러 개의 리뷰가 있을 수 있음
    @ManyToOne
    @JoinColumn(name = "item_id")  // 외래 키 설정 (item_id)
    private Item item;
}
