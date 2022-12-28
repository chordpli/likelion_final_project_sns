package com.likelion.finalproject.service;

import com.likelion.finalproject.domain.dto.*;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.exception.ErrorCode;
import com.likelion.finalproject.exception.SNSAppException;
import com.likelion.finalproject.repository.UserRepository;
import com.likelion.finalproject.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.likelion.finalproject.domain.enums.UserRole.ADMIN;
import static com.likelion.finalproject.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret}")
    private String secretKey;

    private long expireTimeMs = 1000 * 60 * 60;

    public UserJoinResponse join(UserJoinRequest dto) {
        userRepository.findByUserName(dto.getUserName())
                .ifPresent(user -> {
                    throw new SNSAppException(DUPLICATED_USER_NAME, DUPLICATED_USER_NAME.getMessage() );
                });

        User savedUser = userRepository.save(dto.toEntity(encoder.encode(dto.getPassword())));
        return new UserJoinResponse(savedUser.getId(), savedUser.getUserName());
    }

    public UserLoginResponse login(UserLoginRequest dto) {
        // 유저가 있는지 확인
        User user = userRepository.findByUserName(dto.getUserName())
                .orElseThrow(
                        () -> new SNSAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage())
                );

        // 비밀번호가 일치하는지 확인
        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new SNSAppException(INVALID_PASSWORD, INVALID_PASSWORD.getMessage());
        }

        // 토큰 리턴
        return UserLoginResponse.builder()
                .jwt(JwtUtil.createJwt(dto.getUserName(), secretKey, expireTimeMs))
                .build();
    }

    public UserSwithResponse changeUserRoleToAdmin(Integer userId, String name) {
        log.info("service toAdmin userId ={}", userId);
        // 해당 유저가 있는지 확인
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new SNSAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage())
        );

        // 요청을 보낸 user가 존재하는지 확인
        User admin = userRepository.findByUserName(name).orElseThrow(
                () -> new SNSAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage())
        );

        // 해당 유저가 관리자인지 확인
        if (!admin.getUserRole().equals(ADMIN)) {
            throw new SNSAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage());
        }

        userRepository.save(user.changeUserRole(ADMIN));
        return new UserSwithResponse(user.getUserName(), user.getUserRole());
    }
}