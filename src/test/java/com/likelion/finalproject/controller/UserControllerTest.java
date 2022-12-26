package com.likelion.finalproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.finalproject.domain.dto.UserDto;
import com.likelion.finalproject.domain.dto.UserJoinRequest;
import com.likelion.finalproject.domain.dto.UserLoginRequest;
import com.likelion.finalproject.domain.dto.UserLoginResponse;
import com.likelion.finalproject.domain.enums.UserRole;
import com.likelion.finalproject.exception.SNSAppException;
import com.likelion.finalproject.service.UserService;
import com.likelion.finalproject.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.likelion.finalproject.exception.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@WithMockUser
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    private String token;

    @Value("${jwt.secret}")
    private String secretKey;

    @BeforeEach()
    public void getToken() {
        long expireTimeMs = 1000 * 60 * 60;
        token = JwtUtil.createJwt("chordpli", secretKey, System.currentTimeMillis() + expireTimeMs);
    }

    /* 회원가입 */
    @Test
    @DisplayName("회원가입 성공")
    void join_success() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1)
                .userName("jun")
                .password("abcd")
                .userRole(UserRole.USER)
                .build();

        UserJoinRequest request = new UserJoinRequest("jun", "abcd");

        given(userService.join(any()))
                .willReturn(userDto);

        String url = "/api/v1/users/join";
        mockMvc.perform(post(url).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.userId").exists())
                .andExpect(jsonPath("$.result.userId").value(1))
                .andExpect(jsonPath("$.result.userName").exists())
                .andExpect(jsonPath("$.result.userName").value("jun"))
                .andDo(print());

        verify(userService, times(1)).join(any());
    }

    @Test
    @DisplayName("회원가입 실패 - userName 중복")
    void join_fail() throws Exception {
        UserJoinRequest request = new UserJoinRequest("jun", "abcd");

        given(userService.join(any()))
                .willThrow(new SNSAppException(DUPLICATED_USER_NAME, DUPLICATED_USER_NAME.getMessage()));

        String url = "/api/v1/users/join";
        mockMvc.perform(post(url).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isConflict())
                .andDo(print());
    }

    /* 로그인 */
    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1)
                .userName("jun")
                .password("abcd")
                .userRole(UserRole.USER)
                .build();

        UserLoginRequest dto = new UserLoginRequest(userDto.getUserName(), userDto.getPassword());
        UserLoginResponse response = new UserLoginResponse(token);
        given(userService.login(any())).willReturn(response);

        String url = "/api/v1/users/login";
        mockMvc.perform(post(url).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.jwt").exists())
                .andExpect(jsonPath("$.result.jwt").value(token))
                .andDo(print());
        verify(userService, times(1)).login(any());
    }

    @Test
    @DisplayName("로그인 실패_userName 없음")
    void login_fail_empty_user_name() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1)
                .userName("jun")
                .password("abcd")
                .userRole(UserRole.USER)
                .build();

        UserLoginRequest dto = new UserLoginRequest("abc", "bbcd");

        given(userService.login(any()))
                .willThrow(new SNSAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        String url = "/api/v1/users/login";
        mockMvc.perform(post(url).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패_password 오류")
    void login_fail_wrong_password() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1)
                .userName("jun")
                .password("abcd")
                .userRole(UserRole.USER)
                .build();

        UserLoginRequest dto = new UserLoginRequest(userDto.getUserName(), "bbcd");

        given(userService.login(any()))
                .willThrow(new SNSAppException(INVALID_PASSWORD, INVALID_PASSWORD.getMessage()));

        String url = "/api/v1/users/login";
        mockMvc.perform(post(url).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}