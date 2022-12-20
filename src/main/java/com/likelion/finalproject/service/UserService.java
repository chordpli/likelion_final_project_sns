package com.likelion.finalproject.service;

import com.likelion.finalproject.domain.dto.UserDto;
import com.likelion.finalproject.domain.dto.UserJoinRequest;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserDto join(UserJoinRequest dto) {
        userRepository.findByUserName(dto.getUserName())
                .ifPresent(user->{
                    new RuntimeException();
                });

        User savedUser = userRepository.save(dto.toEntity(encoder.encode(dto.getPassword())));
        return UserDto.builder()
                .id(savedUser.getId())
                .userName(savedUser.getUserName())
                .password(savedUser.getPassword())
                .userRole(savedUser.getUserRole())
                .build();
    }
}
