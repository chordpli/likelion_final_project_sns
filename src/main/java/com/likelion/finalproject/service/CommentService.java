package com.likelion.finalproject.service;

import com.likelion.finalproject.domain.dto.*;
import com.likelion.finalproject.domain.entity.Alarm;
import com.likelion.finalproject.domain.entity.Comment;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.repository.AlarmRepository;
import com.likelion.finalproject.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.likelion.finalproject.domain.enums.AlarmType.NEW_COMMENT_ON_POST;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final AlarmRepository alarmRepository;
    private final Services service;


    public CommentWriteResponse writeComment(Integer postId, CommentRequest requset, String userName) {
        User user = service.validateGetUserByUserName(userName);
        Post post = service.validateGetPostById(postId);
        Comment savedComment = Comment.builder()
                .user(user)
                .post(post)
                .comment(requset.getComment())
                .build();
        commentRepository.save(savedComment);
        alarmRepository.save(Alarm.toEntity(user, post, NEW_COMMENT_ON_POST));
        return CommentWriteResponse.of(savedComment);
    }

    public CommentModifyResponse modifyComment(Integer postId, Integer id, CommentRequest request, String userName) {
        User user = service.validateGetUserByUserName(userName);
        Post post = service.validateGetPostById(postId);
        Comment comment = service.validateGetCommentById(id);
        service.validateMatchUsers(user, comment);

        comment.update(request.getComment());

        return CommentModifyResponse.of(commentRepository.save(comment));
    }

    public CommentDeleteResponse deleteComment(Integer postId, Integer id, String userName) {
        Post post = service.validateGetPostById(postId);
        User user = service.validateGetUserByUserName(userName);
        Comment comment = service.validateGetCommentById(id);
        service.validateMatchUsers(user, comment);

        commentRepository.delete(comment);
        return new CommentDeleteResponse("댓글 삭제 완료", id);
    }

    public List<CommentReadResponse> getAllComments(PageRequest pageable, Integer postId) {
        Post post = service.validateGetPostById(postId);

        Page<Comment> comments = commentRepository.findCommentsByPost(post, pageable);

        return comments.stream()
                .map(Comment::toResponse)
                .collect(Collectors.toList());
    }
}