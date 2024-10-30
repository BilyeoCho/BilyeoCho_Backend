package com.bilyeocho.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "User")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id",  nullable = false)
    private String userId;

    @Column(name = "user_name",  nullable = false)
    private String userName;

//    @Column(name = "user_email")
//    private String userEmail;

    @Column(name = "user_password",  nullable = false)
    private String userPassword;

    @Column(name = "user_photo")
    private String userPhoto;



    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> registerItems;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @PreRemove
    public void preRemove() {
        for (Item item : registerItems) {
            item.setUser(null); // 관계 끊기
        }
    }


    @PrePersist
    public void prePersist() {
        if (this.userPhoto == null || this.userPhoto.isEmpty()) {
            this.userPhoto = "none";
        }
        if (this.role == null) {
            this.role = Role.USER;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return userPassword;  // 비밀번호 반환
    }

    @Override
    public String getUsername() {
        return userId;  // 로그인 ID 반환
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // 계정이 만료되지 않음
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // 계정이 잠기지 않음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 자격 증명이 만료되지 않음
    }

    @Override
    public boolean isEnabled() {
        return true;  // 계정 활성화 여부
    }
}

