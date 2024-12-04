package com.bilyeocho.domain;


import com.bilyeocho.domain.enums.ReviewCategory;
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

    @JoinColumn(name = "rate", nullable = false)
    private String rate;

    @JoinColumn(name = "review_photo")
    private String reviewPhoto;

    @JoinColumn(name = "content")
    private String content;

    @JoinColumn(name = "review_category")
    private ReviewCategory reviewcategory;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
}
