package com.bilyeocho.domain;

import com.bilyeocho.domain.Item;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "User")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id",  nullable = false)
    private String userId;

//    @Column(name = "user_name",  nullable = false)
//    private String userName;

//    @Column(name = "user_email")
//    private String userEmail;

    @Column(name = "user_password",  nullable = false)
    private String userPassword;

    @Column(name = "user_photo")
    private String userPhoto;


    //기본 값으로 USER
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> registerItems;
}

