package com.bilyeocho.service;

import com.bilyeocho.domain.User;
import com.bilyeocho.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthenticationService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        User user =  userRepository.findByUserId(memberId)
                .orElseThrow(()-> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return createUserDetails(user);
    }

    public String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // 인증된 사용자 ID 반환
    }

    private UserDetails createUserDetails(User user) {

        return new org.springframework.security.core.userdetails.User(
                user.getUserId(),  // 로그인 ID 필드
                user.getUserPassword(),  // 비밀번호 필드
                user.getAuthorities()  // 권한 리스트
        );
    }
}
