package com.bilyeocho.config;

import com.bilyeocho.jwt.JwtAuthenticationEntryPoint;
import com.bilyeocho.jwt.JwtAuthenticationFilter;
import com.bilyeocho.jwt.JwtTokenProvider;
import com.bilyeocho.service.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserAuthenticationService userAuthenticationService;

    @Bean
    public BCryptPasswordEncoder encoderPwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic(httpBasic -> httpBasic.disable())
                .csrf(csrfConfig -> csrfConfig.disable())
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exHandling -> exHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/join", "/api/login", "/test/**", "/v3/**", "/swagger-ui/**", "/api/item/**", "/api/items", "/api/latest").permitAll()
                                //regist 추가 - 태양
                                .requestMatchers("api/regist", "api/update/**", "api/delete/**","api/rents/**","api/reviews/**").authenticated());

        return http.build();
    }




    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userAuthenticationService);
        authProvider.setPasswordEncoder(encoderPwd());
        return new ProviderManager(authProvider);
    }


}
