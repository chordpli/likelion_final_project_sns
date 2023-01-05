package com.likelion.finalproject.controller.restcontroller;

import com.likelion.finalproject.domain.Response;
import com.likelion.finalproject.domain.dto.*;
import com.likelion.finalproject.service.CommentService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/posts")
@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentRestController {
    private final CommentService commentService;

    /* 댓글 Comment */

    @ApiOperation(value = "댓글 조회")
    @GetMapping("/{postId}/comments")
    public Response<Page<CommentReadResponse>> getComments(@PathVariable Integer postId) {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        List<CommentReadResponse> comments = commentService.getAllComments(pageable, postId);
        return Response.success(new PageImpl<>(comments));
    }

    @ApiOperation(value = "댓글 작성")
    @PostMapping("/{postId}/comments")
    public Response<CommentWriteResponse> writeComment(@PathVariable Integer postId,
                                                       @RequestBody CommentRequest request,
                                                       Authentication authentication) {
        String userName = authentication.getName();
        CommentWriteResponse response = commentService.writeComment(postId, request, userName);
        return Response.success(response);
    }

    @ApiOperation(value = "댓글 수정")
    @PutMapping("/{postId}/comments/{id}")
    public Response<CommentModifyResponse> modifyComment(@PathVariable Integer postId,
                                                         @PathVariable Integer id,
                                                         @RequestBody CommentRequest request,
                                                         Authentication authentication) {
        String userName = authentication.getName();
        CommentModifyResponse response = commentService.modifyComment(postId, id, request, userName);
        return Response.success(response);
    }

    @ApiOperation(value = "댓글 삭제")
    @DeleteMapping("/{postId}/comments/{id}")
    public Response<CommentDeleteResponse> deleteComment(@PathVariable Integer postId,
                                                         @PathVariable Integer id,
                                                         Authentication authentication) {
        String userName = authentication.getName();
        CommentDeleteResponse response = commentService.deleteComment(postId, id, userName);
        return Response.success(response);
    }
}
