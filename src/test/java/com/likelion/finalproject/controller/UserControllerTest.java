package com.likelion.finalproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.finalproject.domain.dto.UserDto;
import com.likelion.finalproject.domain.dto.UserJoinRequest;
import com.likelion.finalproject.domain.enums.UserRole;
import com.likelion.finalproject.exception.ErrorCode;
import com.likelion.finalproject.exception.SNSAppException;
import com.likelion.finalproject.repository.UserRepository;
import com.likelion.finalproject.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.likelion.finalproject.exception.ErrorCode.DUPLICATED_USER_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
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

    @Test
    @DisplayName("회원가입 성공")
    @WithMockUser
    void join_success() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1)
                .userName("jun")
                .password("abcd")
                .userRole(UserRole.USER)
                .build();

        UserJoinRequest dto = new UserJoinRequest("jun", "abcd");

        given(userService.join(any()))
                .willReturn(userDto);

        int id = 1;

        String url = "/api/v1/users/join";
        mockMvc.perform(post(url).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userDto)))
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
    @DisplayName("회원가입 실패")
    @WithMockUser
    void join_fail() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1)
                .userName("jun")
                .password("abcd")
                .userRole(UserRole.USER)
                .build();

        given(userService.join(any()))
                .willThrow(new SNSAppException(DUPLICATED_USER_NAME, DUPLICATED_USER_NAME.getMessage()));

        String url = "/api/v1/users/join";
        mockMvc.perform(post(url).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userDto)))
                .andExpect(status().isConflict());
    }
}