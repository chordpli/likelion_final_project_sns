package com.likelion.finalproject.controller.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.exception.SNSAppException;
import com.likelion.finalproject.fixture.PostFixture;
import com.likelion.finalproject.fixture.UserFixture;
import com.likelion.finalproject.service.LikesService;
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

import java.time.LocalDateTime;

import static com.likelion.finalproject.exception.ErrorCode.NOT_LOGGED_IN;
import static com.likelion.finalproject.exception.ErrorCode.POST_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikesRestController.class)
@WithMockUser
class LikesRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LikesService likeService;

    private String token;
    @Value("${jwt.secret}")
    private String secretKey;
    private String refreshToken;

    public final LocalDateTime time = LocalDateTime.now();

    @BeforeEach()
    public void getToken() {
        long expireTimeMs = 1000 * 60 * 60;
        token = JwtUtil.createJwt(UserFixture.get("chordpli", "1234"), secretKey);
        refreshToken = JwtUtil.createRefreshJwt("chordpli", secretKey);
    }

    @Test
    @DisplayName("좋아요 누르기 성공")
    void success_like() throws Exception{
        User user = UserFixture.get("chordpli", "1234");
        Post post = PostFixture.get(user);

        given(likeService.increaseLike(any(), any())).willReturn("좋아요를 눌렀습니다.");

        String url = String.format("/api/v1/posts/%d/likes", post.getId());
        mockMvc.perform(post(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result").value("좋아요를 눌렀습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("좋아요 누르기 실패 - 로그인하지 않은 경우")
    void fail_like_no_login() throws Exception{
        User user = UserFixture.get("chordpli", "1234");
        Post post = PostFixture.get(user);

        given(likeService.increaseLike(any(), any())).willThrow(new SNSAppException(NOT_LOGGED_IN, NOT_LOGGED_IN.getMessage()));

        String url = String.format("/api/v1/posts/%d/likes", post.getId());
        mockMvc.perform(post(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(1)))
                .andExpect(status().isConflict())
                .andDo(print());
    }

    @Test
    @DisplayName("좋아요 누르기 실패 - 해당 포스트가 없는 경우")
    void fail_like_not_exist_post() throws Exception{
        User user = UserFixture.get("chordpli", "1234");
        Post post = PostFixture.get(user);

        given(likeService.increaseLike(any(), any())).willThrow(new SNSAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));

        String url = String.format("/api/v1/posts/%d/likes", post.getId());
        mockMvc.perform(post(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(1)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}