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
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rate", nullable = false)
    private String rate;

    @Column(name = "review_photo")
    private String reviewPhoto;

    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY) // Cascade 제거
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // Cascade 제거
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
}
