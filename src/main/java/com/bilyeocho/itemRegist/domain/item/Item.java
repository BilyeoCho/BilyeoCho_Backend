package com.bilyeocho.itemRegist.domain.item;

import jakarta.persistence.*;
import com.bilyeocho.itemRegist.domain.user.User;

@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", nullable = false) //pk
    private Long itemId;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "item_photo", nullable = false)
    private String itemPhoto = null;

    @Column(name = "category", nullable = false)
    private String category = null;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Item() {}

    public Item(String itemName, String itemPhoto, User user) {
        this.itemName = itemName;
        this.itemPhoto = itemPhoto;
        this.category = category;
        this.user = user;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPhoto() {
        return itemPhoto;
    }

    public void setItemPhoto(String itemPhoto) {
        this.itemPhoto = itemPhoto;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

