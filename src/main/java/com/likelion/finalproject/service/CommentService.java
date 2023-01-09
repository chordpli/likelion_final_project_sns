package com.likelion.finalproject.service;

import com.likelion.finalproject.domain.dto.comment.*;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.likelion.finalproject.domain.enums.AlarmType.NEW_COMMENT_ON_POST;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final AlarmRepository alarmRepository;
    private final ValidateService service;


    /**
     * 특정 게시물에 코멘트를 작성하는 메서드
     *
     * @param postId 코멘트를 작성할 게시물 id
     * @param request 작성할 코멘트 내용이 담긴 dto
     * @param userName 코멘트를 작성하는 user
     * @return 저장된 comment 내용을 반환합니다.
     */
    @Transactional
    public CommentWriteResponse writeComment(Integer postId, CommentRequest request, String userName) {
        User user = service.validateGetUserByUserName(userName);
        Post post = service.validateGetPostById(postId);
        Comment savedComment = Comment.toEntity(user, post, request);
        commentRepository.save(savedComment);
        alarmRepository.save(Alarm.toEntity(user, post, NEW_COMMENT_ON_POST, savedComment.getId()));
        return CommentWriteResponse.of(savedComment);
    }

    /**
     * 게시물에 작성된 코멘트를 수정하는 메서드
     *
     * @param postId 코멘트가 달려 있는 게시물 id
     * @param id 수정하려는 코멘트의 id
     * @param request 코멘트의 수정 내용을 담은 dto
     * @param userName 코멘트 수정 요청을 보낸 user
     * @return
     */
    @Transactional
    public CommentModifyResponse modifyComment(Integer postId, Integer id, CommentRequest request, String userName) {
        User user = service.validateGetUserByUserName(userName);
        Post post = service.validateGetPostById(postId);
        Comment comment = service.validateGetCommentById(id);
        service.validateMatchUsers(user, comment);

        commentRepository.update(request.getComment(), comment.getId());

        // comment.update(request.getComment());
        //return CommentModifyResponse.of(commentRepository.save(comment));
        return CommentModifyResponse.of(comment);
    }

    /**
     * 특정 게시물에 달린 특정 코멘트를 삭제합니다.
     * @param postId 삭제하려는 코멘트가 작성되어 있는 포스트id
     * @param id 삭제하려는 코멘트 id
     * @param userName 삭제를 요청한 user
     * @return
     */
    @Transactional
    public CommentDeleteResponse deleteComment(Integer postId, Integer id, String userName) {
        Post post = service.validateGetPostById(postId);
        User user = service.validateGetUserByUserName(userName);
        Comment comment = service.validateGetCommentById(id);
        service.validateMatchUsers(user, comment);

        commentRepository.delete(comment);
        alarmRepository.deleteAlarmByCommentId(id);
        return new CommentDeleteResponse("댓글 삭제 완료", id);
    }

    /**
     * 특정 post의 모든 코멘트를 가져오는 메서드
     *
     * @param pageable 페이징 셋팅을 담고 있음
     * @param postId 코멘트가 달려있는 post id
     * @return
     */
    @Transactional
    public List<CommentReadResponse> getAllComments(PageRequest pageable, Integer postId) {
        Post post = service.validateGetPostById(postId);

        Page<Comment> comments = commentRepository.findCommentsByPost(post, pageable);

        return comments.stream()
                .map(Comment::toResponse)
                .collect(Collectors.toList());
    }
}