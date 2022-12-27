package com.likelion.finalproject.config;

import com.likelion.finalproject.exception.CustomAuthenticationEntryPoint;
import com.likelion.finalproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.
                httpBasic().disable()// http basic이 아닌 token으로 인증할 것 이기 때문에 disable 처리
                .csrf().disable()
                .cors().disable()
                .authorizeRequests()
                .antMatchers("/api/v1/users/join", "/api/v1/users/login", "/swagger-ui", "/api/v1/users/switchToAdmin/**", "/", "/index", "/users/join", "/users/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/**","/").authenticated() // /api/v1/** 으로 넘어오는 post 요청들에 대해 인증요청. 순서대로 넘어오기 때문에 위에서 막히면 다 막혀버림. 순서 잘 고려해서 작성.
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
