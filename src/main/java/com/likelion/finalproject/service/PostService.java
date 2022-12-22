package com.likelion.finalproject.service;

import com.likelion.finalproject.domain.dto.PostRequest;
import com.likelion.finalproject.domain.dto.PostResponse;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.exception.ErrorCode;
import com.likelion.finalproject.exception.SNSAppException;
import com.likelion.finalproject.repository.PostRepository;
import com.likelion.finalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostResponse post(PostRequest dto, String userName) {
        // user가 찾아지지 않는다면 등록할 수 없다.
        User user = userRepository.findByUserName(userName)
                .orElseThrow(
                        () -> new SNSAppException(ErrorCode.USERNAME_NOT_FOUND, "일치하지 않은 회원 입니다.")
                );

        Post post = Post.builder()
                .user(user)
                .title(dto.getTitle())
                .body(dto.getBody())
                .build();

        postRepository.save(post);

        return new PostResponse("포스트 등록 완료", post.getId());
    }
}
