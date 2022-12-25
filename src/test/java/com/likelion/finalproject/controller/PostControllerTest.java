package com.likelion.finalproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.finalproject.domain.dto.PostDto;
import com.likelion.finalproject.domain.dto.PostReadResponse;
import com.likelion.finalproject.domain.dto.PostRequest;
import com.likelion.finalproject.domain.dto.PostResponse;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.domain.enums.UserRole;
import com.likelion.finalproject.exception.SNSAppException;
import com.likelion.finalproject.service.PostService;
import com.likelion.finalproject.service.UserService;
import com.likelion.finalproject.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.likelion.finalproject.exception.ErrorCode.INVALID_TOKEN;
import static com.likelion.finalproject.exception.ErrorCode.NOT_EXIST_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@WithMockUser
class PostControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @MockBean
    PostService postService;

    private String token;

    @Value("${jwt.secret}")
    private String secretKey;

    public final LocalDateTime time = LocalDateTime.now();

    @BeforeEach()
    public void getToken() {
        long expireTimeMs = 1000 * 60 * 60;
        token = JwtUtil.createJwt("chordpli", secretKey, System.currentTimeMillis() + expireTimeMs);
    }

    /* 포스트 상세 */
    @Test
    @DisplayName("포스트 상세 보기")
    void post_one_detail() throws Exception {
        User user = User.builder()
                .id(1)
                .userName("chordpli")
                .password("1234")
                .userRole(UserRole.USER)
                .build();

        PostDto dto = PostDto.builder()
                .id(1)
                .title("제목")
                .body("내용")
                .user(user)
                .build();

        int postId = 1;


        PostReadResponse response = PostReadResponse.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .body(dto.getBody())
                .userName(dto.getUser().getUserName())
                .createdAt(time)
                .lastModifiedAt(time)
                .build();


        given(postService.getPost(any())).willReturn(response);

        String url = String.format("/api/v1/posts/%d", postId);

        mockMvc.perform(get(url).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.title").exists())
                .andExpect(jsonPath("$.result.title").value("제목"))
                .andExpect(jsonPath("$.result.body").exists())
                .andExpect(jsonPath("$.result.body").value("내용"))
                .andExpect(jsonPath("$.result.userName").exists())
                .andExpect(jsonPath("$.result.userName").value("chordpli"))
                .andExpect(jsonPath("$.result.createdAt").exists())
                .andExpect(jsonPath("$.result.lastModifiedAt").exists())
                .andDo(print());
        verify(postService, times(1)).getPost(any());

    }

    /* 포스트 등록 */
    @Test
    @DisplayName("포스트 작성 성공")
    void post_success() throws Exception {
        PostRequest dto = new PostRequest("title", "content");
        PostResponse response = new PostResponse("포스트 등록 완료.", 1);

        given(postService.post(any(), any())).willReturn(response);

        String url = "/api/v1/posts";

        mockMvc.perform(post(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.message").value("포스트 등록 완료."))
                .andExpect(jsonPath("$.result.postId").exists())
                .andExpect(jsonPath("$.result.postId").value(1))
                .andDo(print());
        verify(postService, times(1)).post(any(), any());
    }

    @Test
    @DisplayName("포스트 작성 실패")
    void post_fail_no_token() throws Exception {
        PostRequest dto = new PostRequest("title", "content");

        given(postService.post(any(), any())).willThrow(new SNSAppException(NOT_EXIST_TOKEN, "토큰이 존재하지 않습니다."));

        String url = "/api/v1/posts";

        mockMvc.perform(post(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
        verify(postService, times(1)).post(any(), any());
    }

    @Test
    @DisplayName("포스트 작성 실패")
    void post_fail_invalid_token() throws Exception {
        PostRequest dto = new PostRequest("title", "content");
        token = JwtUtil.createJwt("chordpli", secretKey, System.currentTimeMillis());
        given(postService.post(any(), any())).willThrow(new SNSAppException(INVALID_TOKEN, "유효하지 않은 토큰입니다."));

        String url = "/api/v1/posts";

        mockMvc.perform(post(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
        verify(postService, times(1)).post(any(), any());
    }

    /* 포스트 수정 */

    /* 포스트 삭제 */

    /* 포스트 리스트 */


}