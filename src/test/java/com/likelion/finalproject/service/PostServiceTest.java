package com.likelion.finalproject.service;

import com.likelion.finalproject.domain.dto.PostModifyRequest;
import com.likelion.finalproject.domain.dto.PostReadResponse;
import com.likelion.finalproject.domain.dto.PostRequest;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.domain.enums.UserRole;
import com.likelion.finalproject.exception.ErrorCode;
import com.likelion.finalproject.exception.SNSAppException;
import com.likelion.finalproject.fixture.PostFixture;
import com.likelion.finalproject.fixture.UserFixture;
import com.likelion.finalproject.repository.PostRepository;
import com.likelion.finalproject.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostServiceTest {
    private PostService postService;

    private PostRepository postRepository = mock(PostRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);


    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository, userRepository);
    }

    /* 포스트 상세 */
    @Test
    @DisplayName("조회 성공")
    void success_get_post() {
        Post fixture = PostFixture.get();

        when(postRepository.findById(fixture.getId())).thenReturn(Optional.of(fixture));
        PostReadResponse response = postService.getPost(fixture.getId());
        assertEquals(fixture.getUser().getUserName(), response.getUserName());
    }

    /* 포스트 등록 */
    @Test
    @DisplayName("등록 성공")
    void success_post() {
        Post fixture = PostFixture.get();

        PostReadResponse response = PostReadResponse.builder()
                .id(fixture.getId())
                .title(fixture.getTitle())
                .body(fixture.getBody())
                .userName(fixture.getUser().getUserName())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        User mockUser = mock(User.class);
        Post mockPost = mock(Post.class);

        when(userRepository.findByUserName(response.getUserName()))
                .thenReturn(Optional.of(mockUser));
        when(postRepository.save(any()))
                .thenReturn(mockPost);

        Assertions.assertDoesNotThrow(() -> postService.post(new PostRequest(response.getTitle(), response.getBody()), response.getUserName()));
    }

    @Test
    @DisplayName("등록 실패_존재하지 않은 유저")
    void fail_post_not_exist_user() {
        Post fixture = PostFixture.get();
        when(userRepository.findByUserName(fixture.getUser().getUserName())).thenReturn(Optional.empty());
        when(postRepository.save(any())).thenReturn(mock(Post.class));
        SNSAppException exception
                = Assertions.assertThrows(SNSAppException.class,
                () -> postService.post(new PostRequest(fixture.getTitle(), fixture.getBody()), fixture.getUser().getUserName()));
        Assertions.assertEquals(ErrorCode.USERNAME_NOT_FOUND, exception.getErrorCode());
    }

    /* 포스트 수정 */
    @Test
    @DisplayName("수정 실패_포스트가 존재하지 않음")
    void fail_modify_post_not_exist_post() {
        Post fixture = PostFixture.get();
        when(userRepository.findByUserName(fixture.getUser().getUserName())).thenReturn(Optional.of(mock(User.class)));
        when(postRepository.save(any())).thenReturn(Optional.empty());
        SNSAppException exception
                = Assertions.assertThrows(SNSAppException.class,
                () -> postService.modifyPost(fixture.getId(), new PostModifyRequest(fixture.getTitle(), fixture.getBody()), fixture.getUser().getUserName()));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("수정 실패_작성자와 수정을 요청하는 유저가 다름")
    void fail_modify_post_different_Requester_and_author() {
        Post fixture = PostFixture.get();
        User user = User.builder()
                .id(2)
                .userName("unknown")
                .password("1111")
                .userRole(UserRole.USER)
                .build();

        when(postRepository.findById(fixture.getId())).thenReturn(Optional.of(new Post(fixture.getId(), fixture.getBody(), fixture.getTitle(), fixture.getUser())));
        when(userRepository.findByUserName(fixture.getUser().getUserName())).thenReturn(Optional.of(new User(user.getId(), user.getPassword(), user.getUserRole(), user.getUserName())));
        SNSAppException exception
                = Assertions.assertThrows(SNSAppException.class,
                () -> postService.modifyPost(fixture.getId(), new PostModifyRequest(fixture.getTitle(), fixture.getBody()), fixture.getUser().getUserName()));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, exception.getErrorCode());
    }

    @Test
    @DisplayName("수정 성공")
    void success_modify_post(){
        Post fixture = PostFixture.get();
        User user = UserFixture.get("chordpli", "1234");

        when(postRepository.findById(fixture.getId())).thenReturn(Optional.of(fixture));
        when(userRepository.findByUserName(fixture.getUser().getUserName())).thenReturn(Optional.of(user));

        PostModifyRequest request = new PostModifyRequest("수정 제목", "수정 내용");

        fixture.setTitle(request.getTitle());
        fixture.setTitle(request.getBody());

        when(postRepository.save(any()))
                .thenReturn(fixture);

        Assertions.assertDoesNotThrow(() -> postService.modifyPost(fixture.getId(), request, user.getUserName()));
        assertEquals(fixture.getTitle(), request.getTitle());
    }

    /* 포스트 삭제 */
    @Test
    @DisplayName("삭제 실패_유저가 존재하지 않음")
    void fail_delete_post_not_exist_user() {
        Post fixture = PostFixture.get();

        when(userRepository.findByUserName(fixture.getUser().getUserName())).thenReturn(Optional.empty());

        SNSAppException exception
                = Assertions.assertThrows(SNSAppException.class,
                () -> postService.deletePost(fixture.getId(), fixture.getUser().getUserName()));
        Assertions.assertEquals(ErrorCode.USERNAME_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("삭제 실패_포스트 존재하지 않음")
    void fail_delete_post_not_exist_post() {
        Post fixture = PostFixture.get();

        when(userRepository.findByUserName(fixture.getUser().getUserName())).thenReturn(Optional.of(UserFixture.get("chordpli", "1234")));
        when(postRepository.findById(fixture.getId())).thenReturn(Optional.empty());
        SNSAppException exception
                = Assertions.assertThrows(SNSAppException.class,
                () -> postService.deletePost(fixture.getId(), fixture.getUser().getUserName()));

        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("삭제 성공")
    void success_modify_delete(){
        Post fixture = PostFixture.get();
        User user = UserFixture.get("chordpli", "1234");

        when(postRepository.findById(1)).thenReturn(Optional.of(fixture));
        when(userRepository.findByUserName("chordpli")).thenReturn(Optional.of(user));

        assertEquals(fixture.getUser().getUserName(), user.getUserName());
        Assertions.assertDoesNotThrow(() -> postService.deletePost(fixture.getId(), user.getUserName()));
    }
}