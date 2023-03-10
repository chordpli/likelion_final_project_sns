package com.likelion.finalproject.controller.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.finalproject.domain.dto.post.PostModifyRequest;
import com.likelion.finalproject.domain.dto.post.PostReadResponse;
import com.likelion.finalproject.domain.dto.post.PostRequest;
import com.likelion.finalproject.domain.dto.post.PostResponse;
import com.likelion.finalproject.domain.dto.user.UserLoginResponse;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.domain.enums.UserRole;
import com.likelion.finalproject.exception.SNSAppException;
import com.likelion.finalproject.fixture.PostFixture;
import com.likelion.finalproject.fixture.UserFixture;
import com.likelion.finalproject.service.PostService;
import com.likelion.finalproject.utils.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.likelion.finalproject.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostRestController.class)
@WithMockUser
class PostRestControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PostService postService;

    private String token;
    private String refreshToken;
    @Value("${jwt.secret}")
    private String secretKey;


    public final LocalDateTime time = LocalDateTime.now();

    @BeforeEach()
    public void getToken() {
        long expireTimeMs = 1000 * 60 * 60;
        token = JwtUtil.createJwt(UserFixture.get("chordpli", "1234"), secretKey);
        refreshToken = JwtUtil.createRefreshJwt("chordpli", secretKey);
    }

    /* ????????? */
    /* ????????? ?????? */
    @Test
    @DisplayName("????????? ?????? ??????")
    void post_one_detail() throws Exception {
        Post dto = PostFixture.get();
        int postId = dto.getId();
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
                .andExpect(jsonPath("$.result.title").value("title"))
                .andExpect(jsonPath("$.result.body").exists())
                .andExpect(jsonPath("$.result.body").value("body"))
                .andExpect(jsonPath("$.result.userName").exists())
                .andExpect(jsonPath("$.result.userName").value("chordpli"))
                .andExpect(jsonPath("$.result.createdAt").exists())
                .andExpect(jsonPath("$.result.lastModifiedAt").exists())
                .andDo(print());
        verify(postService, times(1)).getPost(any());

    }

    /* ????????? ?????? */
    @Test
    @DisplayName("????????? ?????? ??????")
    void post_success() throws Exception {
        UserLoginResponse user = new UserLoginResponse(token, refreshToken);

        PostRequest dto = new PostRequest("title", "content");
        PostResponse response = new PostResponse("????????? ?????? ??????.", 1);

        given(postService.post(any(), any())).willReturn(response);

        String url = "/api/v1/posts";

        mockMvc.perform(post(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + user.getJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.message").value("????????? ?????? ??????."))
                .andExpect(jsonPath("$.result.postId").exists())
                .andExpect(jsonPath("$.result.postId").value(1))
                .andDo(print());
        verify(postService, times(1)).post(any(), any());
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void post_fail_no_token() throws Exception {
        PostRequest dto = new PostRequest("title", "content");

        given(postService.post(any(), any())).willThrow(new SNSAppException(INVALID_PERMISSION, "????????? ???????????? ????????????."));

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
    @DisplayName("????????? ?????? ??????")
    void post_fail_invalid_token() throws Exception {
        User user = UserFixture.get("chordpli", "1234");
        PostRequest dto = new PostRequest("title", "content");
        token = JwtUtil.createJwt(UserFixture.get("chordpli", "1234"), secretKey);
        given(postService.post(any(), any())).willThrow(new SNSAppException(INVALID_TOKEN, "???????????? ?????? ???????????????."));

        String url = "/api/v1/posts";

        mockMvc.perform(post(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
        verify(postService, times(1)).post(any(), any());
    }

    /* ????????? ?????? */
    @Test
    @DisplayName("????????? ?????? ??????_?????? ??????")
    void fail_post_modify_authentication_failed() throws Exception {
        User user = UserFixture.get("chordpli", "1234");
        token = JwtUtil.createJwt(UserFixture.get("chordpli", "1234"), secretKey);
        PostModifyRequest request = new PostModifyRequest("title", "content");
        willThrow(new SNSAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage())).given(postService).modifyPost(any(), any(), any());

        Integer postId = 1;
        String url = String.format("/api/v1/posts/%d", postId);

        mockMvc.perform(put(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
        verify(postService, times(1)).modifyPost(any(), any(), any());
    }

    @Test
    @DisplayName("????????? ?????? ??????_????????? ?????????")
    void fail_post_modify_mismatch_author() throws Exception {
        PostModifyRequest dto = new PostModifyRequest("title", "content");
        willThrow(new SNSAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage())).given(postService).modifyPost(any(), any(), any());

        Integer postId = 1;
        String url = String.format("/api/v1/posts/%d", postId);

        mockMvc.perform(put(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
        verify(postService, times(1)).modifyPost(any(), any(), any());
    }

    @Test
    @DisplayName("????????? ?????? ??????_DB ??????")
    void fail_post_modify_db_error() throws Exception {
        PostModifyRequest dto = new PostModifyRequest("title", "content");
        willThrow(new SNSAppException(DATABASE_ERROR, DATABASE_ERROR.getMessage())).given(postService).modifyPost(any(), any(), any());
        Integer postId = 1;
        String url = String.format("/api/v1/posts/%d", postId);

        mockMvc.perform(put(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isInternalServerError())
                .andDo(print());
        verify(postService, times(1)).modifyPost(any(), any(), any());
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void success_post_modify() throws Exception {
        User user = UserFixture.get("chordpli", "1234");
        Post post = PostFixture.get(user);

        PostModifyRequest request = new PostModifyRequest("title", "body");

        willDoNothing().given(postService).modifyPost(any(), any(), any());

        Integer postId = 1;
        String url = String.format("/api/v1/posts/%d", postId);

        mockMvc.perform(put(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.postId").exists())
                .andExpect(jsonPath("$.result.postId").value(1))
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.message").value("????????? ?????? ??????"))
                .andDo(print());

        assertEquals(post.getTitle(), request.getTitle());
        assertEquals(post.getBody(), request.getBody());
    }


    /* ????????? ?????? */
    @Test
    @DisplayName("????????? ?????? ??????_?????? ??????")
    void fail_post_delete_authentication_failed() throws Exception {
        User user = UserFixture.get("chordpli", "1234");
        token = JwtUtil.createJwt(UserFixture.get("chordpli", "1234"), secretKey);
        willThrow(new SNSAppException(INVALID_TOKEN, "???????????? ?????? ???????????????.")).given(postService).deletePost(any(), any());

        Integer postId = 1;
        String url = String.format("/api/v1/posts/%d", postId);

        mockMvc.perform(delete(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(1)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
        verify(postService, times(1)).deletePost(any(), any());
    }

    @Test
    @DisplayName("????????? ?????? ??????_????????? ?????????")
    void fail_post_delete_mismatch_author() throws Exception {
        willThrow(new SNSAppException(INVALID_PERMISSION, "???????????? ????????? ????????????.")).given(postService).deletePost(any(), any());
        //doThrow(new SNSAppException(INVALID_PERMISSION, "???????????? ????????? ????????????.")).when(postService).deletePost(any(), any());

        Integer postId = 1;
        String url = String.format("/api/v1/posts/%d", postId);

        mockMvc.perform(delete(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postId)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
        verify(postService, times(1)).deletePost(any(), any());
    }

    @Test
    @DisplayName("????????? ?????? ??????_DB ??????")
    void fail_post_delete_db_error() throws Exception {

        willThrow(new SNSAppException(DATABASE_ERROR, "????????? ???????????? ????????? ?????????????????????.")).given(postService).deletePost(any(), any());

        Integer postId = 1;
        String url = String.format("/api/v1/posts/%d", postId);

        mockMvc.perform(delete(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postId)))
                .andExpect(status().isInternalServerError())
                .andDo(print());
        verify(postService, times(1)).deletePost(any(), any());
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void success_post_delete() throws Exception {
        doNothing().when(postService).deletePost(any(), any());

        Integer postId = 1;
        String url = String.format("/api/v1/posts/%d", postId);

        mockMvc.perform(delete(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.postId").exists())
                .andExpect(jsonPath("$.result.postId").value(1))
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.message").value("????????? ?????? ??????"))
                .andDo(print());
    }

    /* ????????? ????????? */
    @Test
    @DisplayName("????????? ????????? ?????? ??????_0?????? 1????????? ????????? ????????? ???")
    void success_post_list() throws Exception {
        List<PostReadResponse> posts = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("createdAt").descending());

        User user = User.builder()
                .id(1)
                .userName("chordpli")
                .password("1234")
                .userRole(UserRole.USER)
                .build();

        Post post1 = Post.builder()
                .id(1)
                .title("title1")
                .body("body1")
                .user(user)
                .build();
        post1.setCreatedAt(LocalDateTime.now());
        post1.setLastModifiedAt(LocalDateTime.now());

        Thread.sleep(1000);

        Post post2 = Post.builder()
                .id(2)
                .title("title1")
                .body("body1")
                .user(user)
                .build();

        post2.setCreatedAt(LocalDateTime.now());
        post2.setLastModifiedAt(LocalDateTime.now());

        PostReadResponse response1 = post1.toResponse();
        PostReadResponse response2 = post2.toResponse();

        posts.add(response1);
        posts.add(response2);

        given(postService.getAllPost(pageRequest)).willReturn(posts);

        String url = String.format("/api/v1/posts/");

        mockMvc.perform(get(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(pageRequest)))
                .andExpect(status().isOk())
                .andDo(print());

        Assertions.assertTrue(posts.get(1).getCreatedAt().isAfter(posts.get(0).getCreatedAt()));
    }


}