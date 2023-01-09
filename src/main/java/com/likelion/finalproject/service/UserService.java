package com.likelion.finalproject.service;

import com.likelion.finalproject.config.redis.RedisDao;
import com.likelion.finalproject.domain.dto.user.*;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.exception.SNSAppException;
import com.likelion.finalproject.repository.UserRepository;
import com.likelion.finalproject.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.likelion.finalproject.domain.enums.UserRole.ADMIN;
import static com.likelion.finalproject.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final ValidateService validateService;
    private final BCryptPasswordEncoder encoder;

    private final RedisDao redisDao;

    @Value("${jwt.secret}")
    private String secretKey;

    private long expireTimeMs = 1000 * 60 * 60;

    /**
     * request에 담긴 가입 정보로 회원가입을 진행하는 메서드
     *
     * @param request 가입하는 회원의 정보를 담은 dto
     * @return UserJoinResponse
     */
    @Transactional
    public UserJoinResponse join(UserJoinRequest request) {
        validateService.validateDuplicatedUser(request);

        User savedUser = userRepository.save(request.toEntity(encoder.encode(request.getPassword())));
        return new UserJoinResponse(savedUser.getId(), savedUser.getUserName());
    }


    /**
     * request에 담긴 회원 정보로 로그인을 진행하는 메서드
     *
     * @param request 로그인하려는 회원의 정보를 담은 dto
     * @return Token이 담겨있는 UserLoginResponse 반환
     */
    @Transactional
    public UserLoginResponse login(UserLoginRequest request) {
        // 유저가 있는지 확인
        User user = validateService.validateGetExistingUser(request);

        // 비밀번호가 일치하는지 확인
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new SNSAppException(INVALID_PASSWORD, INVALID_PASSWORD.getMessage());
        }
        String token = JwtUtil.createJwt(user, secretKey);
        String refreshToken = JwtUtil.createRefreshJwt(user.getUserName(), secretKey);

        redisDao.setValues("RTK:" + user.getUserName(), refreshToken);

        // 토큰 리턴
        return UserLoginResponse.builder()
                .jwt(token)
                .refreshToken(refreshToken)
                .build();
    }


    /**
     * 관리자 회원이 특정 회원을 ADMIN(관리자)로 권한을 변경하는 메서드
     *
     * @param userId 권한을 변경 받는 USER
     * @param name 관리자 ADMIN
     * @return
     */
    @Transactional
    public UserSwithResponse changeUserRoleToAdmin(Integer userId, String name) {
        log.info("service toAdmin userId ={}", userId);
        // 해당 유저가 있는지 확인
        User user = userRepository.findById(userId).orElseThrow(
                () -> new SNSAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage())
        );

        // 요청을 보낸 user가 존재하는지 확인
        User admin = validateService.validateGetUserByUserName(name);

        // 해당 유저가 관리자인지 확인
        if (!admin.getUserRole().equals(ADMIN)) {
            throw new SNSAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage());
        }

        userRepository.save(user.changeUserRole(ADMIN));
        return new UserSwithResponse(user.getUserName(), user.getUserRole());
    }
}