package com.likelion.finalproject.service;

import com.likelion.finalproject.domain.entity.Comment;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.exception.SNSAppException;
import com.likelion.finalproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.likelion.finalproject.domain.enums.UserRole.ADMIN;
import static com.likelion.finalproject.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ValidateService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    /**
     * 해당 유저가 관리자인지, 작성자와 삭제 요청자가 같은 사람인지 확인합니다.
     * 관리자가 아니거나, 작성자와 요청자가 일치하지 않을 경우 예외를 발생합니다.
     *
     * @param user user Entity를 입력 받습니다.
     * @param post post Entity를 입력 받습니다.
     */
    public void validateCheckAdminAndEqualWriter(User user, Post post) {
        if (!user.getUserRole().equals(ADMIN) && !user.getId().equals(post.getUser().getId())) {
            throw new SNSAppException(INVALID_PERMISSION, INVALID_PERMISSION.getMessage());
        }
    }

    /**
     * postId를 기반으로 Post의 정보를 찾습니다.
     * Post 정보가 없다면 예외를 발생합니다.
     *
     * @param postId post의 ID를 입력받습니다.
     * @return
     */
    public Post validateGetPostById(Integer postId) {
        return postRepository.findById(postId)
                .orElseThrow(
                        () -> new SNSAppException(POST_NOT_FOUND, POST_NOT_FOUND.getMessage())
                );
    }

    /**
     * userName을 기반으로 User의 정보를 찾습니다.
     * User의 정보가 없다면 예외를 발생합니다.
     *
     * @param userName 사용자 ID를 입력받습니다.
     * @return
     */
    public User validateGetUserByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(
                        () -> new SNSAppException(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND.getMessage())
                );
    }

    public Comment validateGetCommentById(Integer id) {
        return commentRepository.findById(id)
                .orElseThrow(
                        () -> new SNSAppException(COMMENT_NOT_FOUND, COMMENT_NOT_FOUND.getMessage())
                );
    }

    public void validateMatchUsers(User user, Comment comment) {
        if (!Objects.equals(user.getId(), comment.getUser().getId())) {
            throw new SNSAppException(MISMATCH_USER, MISMATCH_USER.getMessage());
        }
    }
}
