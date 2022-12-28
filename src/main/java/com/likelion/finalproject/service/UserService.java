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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
                    throw new SNSAppException(ErrorCode.DUPLICATED_USER_NAME, String.format("userName = %s", dto.getUserName()));
                });

        User savedUser = userRepository.save(dto.toEntity(encoder.encode(dto.getPassword())));
        return new UserJoinResponse(savedUser.getId(), savedUser.getUserName());
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

    public UserSwithResponse toAdmin(Integer userId, String name) {
        log.info("service toAdmin userId ={}", userId);
        // 해당 유저가 있는지 확인
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new SNSAppException(ErrorCode.USERNAME_NOT_FOUND, "해당 유저가 존재하지 않습니다.")
        );

        // 요청을 보낸 user가 관리자인지 확인
        User admin = userRepository.findByUserName(name).orElseThrow(
                () -> new SNSAppException(ErrorCode.USERNAME_NOT_FOUND, "요청을 보낸 유저가 존재하지 않습니다.")
        );

        // 해당 유저가 관리자인지 확인
        if (!admin.getUserRole().equals(UserRole.ADMIN)) {
            throw new SNSAppException(ErrorCode.INVALID_PERMISSION, "권한이 없습니다.");
        }

        user.setUserRole(UserRole.ADMIN);
        user.setLastModifiedAt(LocalDateTime.now());
        userRepository.save(user);
        return new UserSwithResponse(user.getUserName(), user.getUserRole());
    }
}