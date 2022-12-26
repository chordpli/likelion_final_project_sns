package com.likelion.finalproject.service;

import com.likelion.finalproject.domain.dto.*;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.domain.enums.UserRole;
import com.likelion.finalproject.exception.ErrorCode;
import com.likelion.finalproject.exception.SNSAppException;
import com.likelion.finalproject.repository.UserRepository;
import com.likelion.finalproject.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret}")
    private String secretKey;

    private long expireTimeMs = 1000 * 60 * 60;

    public UserDto join(UserJoinRequest dto) {
        userRepository.findByUserName(dto.getUserName())
                .ifPresent(user -> {
                    throw new SNSAppException(ErrorCode.DUPLICATED_USER_NAME, String.format("userName = %s", dto.getUserName()));
                });

        User savedUser = userRepository.save(dto.toEntity(encoder.encode(dto.getPassword())));
        return UserDto.builder()
                .id(savedUser.getId())
                .userName(savedUser.getUserName())
                .password(savedUser.getPassword())
                .userRole(savedUser.getUserRole())
                .build();
    }

    public UserLoginResponse login(UserLoginRequest dto) {
        // 유저가 있는지 확인
        User user = userRepository.findByUserName(dto.getUserName())
                .orElseThrow(
                        () -> new SNSAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s는 회원이 아닙니다.", dto.getUserName()))
                );

        // 비밀번호가 일치하는지 확인
        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new SNSAppException(ErrorCode.INVALID_PASSWORD, String.format("userName 또는 password가 일치하지 않습니다."));
        }

        // 토큰 리턴
        return UserLoginResponse.builder()
                .jwt(JwtUtil.createJwt(dto.getUserName(), secretKey, expireTimeMs))
                .build();
    }

    public UserSwithResponse toAdmin(Integer userId) {
        log.info("service toAdmin userId ={}", userId);
        // 해당 유저가 있는지 확인
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new SNSAppException(ErrorCode.USERNAME_NOT_FOUND, "해당 유저가 존재하지 않습니다.")
        );
        log.info("service toAdmin userRole ={}", user.getUserRole());
        user.setUserRole(UserRole.ADMIN);
        user.setLastModifiedAt(LocalDateTime.now());
        log.info("service toAdmin change userRole ={}", user.getUserRole());
        userRepository.save(user);

        return new UserSwithResponse(user.getUserName(), user.getUserRole());
    }
}