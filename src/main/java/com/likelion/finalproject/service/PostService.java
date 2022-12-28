package com.likelion.finalproject.service;

import com.likelion.finalproject.domain.dto.PostModifyRequest;
import com.likelion.finalproject.domain.dto.PostReadResponse;
import com.likelion.finalproject.domain.dto.PostRequest;
import com.likelion.finalproject.domain.dto.PostResponse;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.exception.SNSAppException;
import com.likelion.finalproject.repository.PostRepository;
import com.likelion.finalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.likelion.finalproject.domain.enums.UserRole.ADMIN;
import static com.likelion.finalproject.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostResponse post(PostRequest dto, String userName) {
        if (userName == null) {
            throw new SNSAppException(NOT_EXIST_TOKEN, NOT_EXIST_TOKEN.getMessage());
        }

        // user가 찾아지지 않는다면 등록할 수 없다.
        User user = userRepository.findByUserName(userName)
                .orElseThrow(
                        () -> new SNSAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage())
                );

        Post post = Post.builder()
                .user(user)
                .title(dto.getTitle())
                .body(dto.getBody())
                .build();

        postRepository.save(post);
        return new PostResponse("포스트 등록 완료", post.getId());
    }

    public PostReadResponse getPost(Integer postId) {
        Post readPost = postRepository.findById(postId)
                .orElseThrow(
                        () -> new SNSAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage())
                );

        return PostReadResponse.of(readPost);
    }

    public List<PostReadResponse> getAllPost(PageRequest pageRequest) {
        Page<Post> posts = postRepository.findAll(pageRequest);
        System.out.println(posts.getPageable());
        return posts.stream()
                .map(Post::toResponse)
                .collect(Collectors.toList());
    }

    public void modifyPost(Integer postId, PostModifyRequest dto, String userName) throws SNSAppException {
        if (userName == null) {
            throw new SNSAppException(NOT_EXIST_TOKEN, NOT_EXIST_TOKEN.getMessage());
        }

        // user가 찾아지지 않는다면 수정할 수 없다.
        User user = userRepository.findByUserName(userName)
                .orElseThrow(
                        () -> new SNSAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage())
                );

        // post가 찾아지지 않는다면 수정할 수 없다.
        Post post = postRepository.findById(postId)
                .orElseThrow(
                        () -> new SNSAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage())
                );
        // User가 관리자가 아닌데, User와 Post를 작성한 User가 다르면 수정할 수 없다.
        if (!user.getUserRole().equals(ADMIN) && !user.getId().equals(post.getUser().getId())) {
            throw new SNSAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage());
        }

        postRepository.save(dto.toEntity(postId, post.getUser()));
    }

    public void deletePost(Integer postId, String userName) {
        if (userName == null) {
            throw new SNSAppException(NOT_EXIST_TOKEN, NOT_EXIST_TOKEN.getMessage());
        }

        // user가 찾아지지 않는다면 삭제할 수 없다.
        User user = userRepository.findByUserName(userName)
                .orElseThrow(
                        () -> new SNSAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage())
                );

        // post가 찾아지지 않는다면 삭제할 수 없다.
        Post post = postRepository.findById(postId)
                .orElseThrow(
                        () -> new SNSAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage())
                );

        // User가 관리자가 아닌데, User와 Post를 작성한 User가 다르면 삭제할 수 없다.
        if (!user.getUserRole().equals(ADMIN) && !user.getId().equals(post.getUser().getId())) {
            throw new SNSAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage());
        }

        // Post를 찾는 findById 메서드를 사용하였으므로 deleteById가 아닌 Delete 사용.
        postRepository.delete(post);
    }
}