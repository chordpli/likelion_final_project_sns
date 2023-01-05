package com.likelion.finalproject.controller.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.finalproject.domain.dto.CommentDeleteResponse;
import com.likelion.finalproject.domain.dto.CommentModifyResponse;
import com.likelion.finalproject.domain.dto.CommentRequest;
import com.likelion.finalproject.domain.dto.CommentWriteResponse;
import com.likelion.finalproject.domain.entity.Comment;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.exception.SNSAppException;
import com.likelion.finalproject.fixture.CommentFixture;
import com.likelion.finalproject.fixture.PostFixture;
import com.likelion.finalproject.fixture.UserFixture;
import com.likelion.finalproject.service.CommentService;
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

import static com.likelion.finalproject.exception.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentRestController.class)
@WithMockUser
class CommentRestControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CommentService commentService;

    private String token;
    @Value("${jwt.secret}")
    private String secretKey;

    public final LocalDateTime time = LocalDateTime.now();

    @BeforeEach()
    public void getToken() {
        long expireTimeMs = 1000 * 60 * 60;
        token = JwtUtil.createJwt("chordpli", secretKey, System.currentTimeMillis() + expireTimeMs);
    }

    /* 코멘트 Comment */
    /* 댓글 등록 */

    @Test
    @DisplayName("댓글 작성 성공")
    void success_write_comment() throws Exception {
        User user = UserFixture.get("chordpli", "1234");
        Post post = PostFixture.get(user);
        CommentRequest request = new CommentRequest("댓글 작성");
        CommentWriteResponse response = CommentWriteResponse.builder()
                .id(1)
                .comment(request.getComment())
                .userName(user.getUserName())
                .postId(post.getId())
                .createdAt(LocalDateTime.now())
                .build();

        given(commentService.writeComment(any(), any(), any())).willReturn(response);

        String url = String.format("/api/v1/posts/%d/comments", post.getId());


        mockMvc.perform(post(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.comment").exists())
                .andExpect(jsonPath("$.result.comment").value("댓글 작성"))
                .andExpect(jsonPath("$.result.postId").exists())
                .andExpect(jsonPath("$.result.postId").value(1))
                .andExpect(jsonPath("$.result.createdAt").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 작성 실패 - 로그인 하지 않은 경우")
    void fail_write_comment_no_login() throws Exception {
        User user = UserFixture.get("chordpli", "1234");
        Post post = PostFixture.get(user);
        CommentRequest request = new CommentRequest("댓글 작성");
        CommentWriteResponse response = CommentWriteResponse.builder()
                .id(1)
                .comment(request.getComment())
                .userName(user.getUserName())
                .postId(post.getId())
                .createdAt(LocalDateTime.now())
                .build();

        given(commentService.writeComment(any(), any(), any())).willThrow(new SNSAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage()));

        String url = String.format("/api/v1/posts/%d/comments", post.getId());


        mockMvc.perform(post(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 작성 실패 - 게시물이 존재하지 않는 경우")
    void fail_write_comment_not_exist_post() throws Exception {
        User user = UserFixture.get("chordpli", "1234");
        Post post = PostFixture.get(user);
        CommentRequest request = new CommentRequest("댓글 작성");
        CommentWriteResponse response = CommentWriteResponse.builder()
                .id(1)
                .comment(request.getComment())
                .userName(user.getUserName())
                .postId(post.getId())
                .createdAt(LocalDateTime.now())
                .build();

        given(commentService.writeComment(any(), any(), any())).willThrow(new SNSAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage()));

        String url = String.format("/api/v1/posts/%d/comments", post.getId());


        mockMvc.perform(post(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    /* 댓글 수정 */
    @Test
    @DisplayName("댓글 수정 성공")
    void success_modify_comment() throws Exception {
        User user = UserFixture.get("chordpli", "1234");
        Post post = PostFixture.get(user);
        Comment comment = CommentFixture.get(user, post);
        CommentRequest request = new CommentRequest("댓글 수정쓰");
        CommentModifyResponse response = CommentModifyResponse.builder()
                .id(1)
                .comment(request.getComment())
                .userName(user.getUserName())
                .postId(post.getId())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        given(commentService.modifyComment(any(), any(), any(), any())).willReturn(response);

        String url = String.format("/api/v1/posts/%d/comments/%d", post.getId(), comment.getId());


        mockMvc.perform(put(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.comment").exists())
                .andExpect(jsonPath("$.result.comment").value("댓글 수정쓰"))
                .andExpect(jsonPath("$.result.postId").exists())
                .andExpect(jsonPath("$.result.postId").value(1))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.createdAt").exists())
                .andExpect(jsonPath("$.result.lastModifiedAt").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 인증 실패")
    void fail_modify_comment_certification() throws Exception {
        User user = UserFixture.get("chordpli", "1234");
        Post post = PostFixture.get(user);
        Comment comment = CommentFixture.get(user, post);
        CommentRequest request = new CommentRequest("댓글 수정쓰");

        given(commentService.modifyComment(any(), any(), any(), any())).willThrow(new SNSAppException(INVALID_TOKEN, INVALID_TOKEN.getMessage()));

        String url = String.format("/api/v1/posts/%d/comments/%d", post.getId(), comment.getId());

        mockMvc.perform(put(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 댓글 불일치")
    void fail_modify_comment_mismatch_comment() throws Exception {
        User user = UserFixture.get("chordpli", "1234");
        Post post = PostFixture.get(user);
        Comment comment = CommentFixture.get(user, post);
        CommentRequest request = new CommentRequest("댓글 수정쓰");

        given(commentService.modifyComment(any(), any(), any(), any())).willThrow(new SNSAppException(MISMATCH_COMMENT, MISMATCH_COMMENT.getMessage()));

        String url = String.format("/api/v1/posts/%d/comments/%d", post.getId(), comment.getId());

        mockMvc.perform(put(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 작성자 불일치")
    void fail_modify_comment_mismatch_writer() throws Exception {
        User user = UserFixture.get("chordpli", "1234");
        Post post = PostFixture.get(user);
        Comment comment = CommentFixture.get(user, post);
        CommentRequest request = new CommentRequest("댓글 수정쓰");

        given(commentService.modifyComment(any(), any(), any(), any())).willThrow(new SNSAppException(MISMATCH_USER, MISMATCH_USER.getMessage()));

        String url = String.format("/api/v1/posts/%d/comments/%d", post.getId(), comment.getId());

        mockMvc.perform(put(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 데이터베이스 에러")
    void fail_modify_comment_db_error() throws Exception {
        User user = UserFixture.get("chordpli", "1234");
        Post post = PostFixture.get(user);
        Comment comment = CommentFixture.get(user, post);
        CommentRequest request = new CommentRequest("댓글 수정쓰");

        given(commentService.modifyComment(any(), any(), any(), any())).willThrow(new SNSAppException(DATABASE_ERROR, DATABASE_ERROR.getMessage()));

        String url = String.format("/api/v1/posts/%d/comments/%d", post.getId(), comment.getId());

        mockMvc.perform(put(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    /* 댓글 수정 */
    @Test
    @DisplayName("댓글 삭제 성공")
    void success_delete_comment() throws Exception {
        User user = UserFixture.get("chordpli", "1234");
        Post post = PostFixture.get(user);
        Comment comment = CommentFixture.get(user, post);

        given(commentService.deleteComment(any(), any(), any())).willReturn(new CommentDeleteResponse("댓글 삭제 완료", 1));

        String url = String.format("/api/v1/posts/%d/comments/%d", post.getId(), comment.getId());

        mockMvc.perform(delete(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.message").value("댓글 삭제 완료"))
                .andDo(print());
    }

    /* 댓글 조회 */
    @Test
    @DisplayName("댓글 조회 성공")
    @WithMockUser
    void success_get_comment() throws Exception {
        String url = String.format("/api/v1/posts/%d/comments", 1);

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print());

/*        ArgumentCaptor<PageRequest> pageableCaptor = ArgumentCaptor.forClass(PageRequest.class);
        verify(postService).getAllPost(pageableCaptor.capture());
        PageRequest pageable = (PageRequest) pageableCaptor.getValue();

        assertEquals(Sort.by(DESC, "createdAt"), pageable.getSort());*/
    }

}