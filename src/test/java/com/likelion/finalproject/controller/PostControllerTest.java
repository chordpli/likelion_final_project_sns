package com.likelion.finalproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.finalproject.domain.dto.*;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.domain.enums.UserRole;
import com.likelion.finalproject.exception.SNSAppException;
import com.likelion.finalproject.fixture.UserFixture;
import com.likelion.finalproject.service.PostService;
import com.likelion.finalproject.service.UserService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        User user = UserFixture.get("chordpli", "1234");

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

        given(postService.post(any(), any())).willThrow(new SNSAppException(INVALID_PERMISSION, "토큰이 존재하지 않습니다."));

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
        User user = UserFixture.get("chordpli", "1234");
        PostRequest dto = new PostRequest("title", "content");
        String token = JwtUtil.createJwt(user.getUserName(), secretKey, System.currentTimeMillis());
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
    @Test
    @DisplayName("포스트 수정 실패_인증 실패")
    void fail_post_modify_authentication_failed() throws Exception {
        User user = UserFixture.get("chordpli", "1234");
        token = JwtUtil.createJwt(user.getUserName(), secretKey, System.currentTimeMillis());
        PostModifyRequest dto = new PostModifyRequest("title", "content");
        given(postService.modifyPost(any(), any(), any())).willThrow(new SNSAppException(INVALID_PERMISSION, "유효하지 않은 토큰입니다."));

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
    @DisplayName("포스트 수정 실패_작성자 불일치")
    void fail_post_modify_mismatch_author() throws Exception {
        PostModifyRequest dto = new PostModifyRequest("title", "content");
        given(postService.modifyPost(any(), any(), any())).willThrow(new SNSAppException(INVALID_PERMISSION, "사용자가 권한이 없습니다."));

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
    @DisplayName("포스트 수정 실패_DB 에러")
    void fail_post_modify_db_error() throws Exception {
        PostModifyRequest dto = new PostModifyRequest("title", "content");
        given(postService.modifyPost(any(), any(), any())).willThrow(new SNSAppException(DATABASE_ERROR, "데이터 베이스에 에러가 발생하였습니다."));

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
    @DisplayName("포스트 수정 성공")
    void success_post_modify() throws Exception {
        User user = User.builder()
                .id(1)
                .userName("chordpli")
                .password("1234")
                .userRole(UserRole.USER)
                .build();

        Post post = Post.builder()
                .id(1)
                .title("제목")
                .body("내용")
                .user(user)
                .build();

        PostModifyRequest dto = new PostModifyRequest("title", "content");

        post.setTitle(dto.getTitle());
        post.setBody(dto.getBody());

        given(postService.modifyPost(any(), any(), any())).willReturn(post);

        Integer postId = 1;
        String url = String.format("/api/v1/posts/%d", postId);

        mockMvc.perform(put(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.postId").exists())
                .andExpect(jsonPath("$.result.postId").value(1))
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.message").value("포스트 수정 완료"))
                .andDo(print());
    }


    /* 포스트 삭제 */
    @Test
    @DisplayName("포스트 삭제 실패_인증 실패")
    void fail_post_delete_authentication_failed() throws Exception {
        User user = UserFixture.get("chordpli", "1234");
        token = JwtUtil.createJwt(user.getUserName(), secretKey, System.currentTimeMillis());
        willThrow(new SNSAppException(INVALID_TOKEN, "유효하지 않은 토큰입니다.")).given(postService).deletePost(any(), any());

        Integer postId = 1;
        String url = String.format("/api/v1/posts/%d", postId);

        mockMvc.perform(delete(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(1)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
        verify(postService, times(1)).deletePost(any(), any());
    }

    @Test
    @DisplayName("포스트 삭제 실패_작성자 불일치")
    void fail_post_delete_mismatch_author() throws Exception {
        willThrow(new SNSAppException(INVALID_PERMISSION, "사용자가 권한이 없습니다.")).given(postService).deletePost(any(), any());
        //doThrow(new SNSAppException(INVALID_PERMISSION, "사용자가 권한이 없습니다.")).when(postService).deletePost(any(), any());

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
    @DisplayName("포스트 삭제 실패_DB 에러")
    void fail_post_delete_db_error() throws Exception {

        willThrow(new SNSAppException(DATABASE_ERROR, "데이터 베이스에 에러가 발생하였습니다.")).given(postService).deletePost(any(), any());

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
    @DisplayName("포스트 삭제 성공")
    void success_post_delete() throws Exception {
        postService = mock(PostService.class);
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
                .andExpect(jsonPath("$.result.message").value("포스트 삭제 완료"))
                .andDo(print());
    }

    /* 포스트 리스트 */
    @Test
    @DisplayName("포스트 리스트 조회 성공_0번이 1번보다 날짜가 최신일 때")
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