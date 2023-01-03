package com.likelion.finalproject.service;

import com.likelion.finalproject.domain.dto.CommentModifyResponse;
import com.likelion.finalproject.domain.dto.CommentRequest;
import com.likelion.finalproject.domain.dto.CommentWriteResponse;
import com.likelion.finalproject.domain.entity.Comment;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
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
}
