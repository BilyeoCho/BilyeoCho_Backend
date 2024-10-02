package com.bilyeocho.domain;

import com.bilyeocho.domain.Item;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //내부 식별자 수정 필
    private Long id;

    //사용자가 회원가입 시 입력하는 사용자 ID 수정 필
    @Column(name = "user_identifier")
    private String userIdentifier;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_email")
    private String userEmail;

    //나중에 암호화 수정 필요
    @Column(name = "user_password")
    private String userPassword;

    @Column(name = "user_photo")
    private String userPhoto;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> registerItems;
}

