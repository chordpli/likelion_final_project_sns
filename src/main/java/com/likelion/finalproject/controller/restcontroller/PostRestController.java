package com.likelion.finalproject.controller.restcontroller;

import com.likelion.finalproject.domain.Response;
import com.likelion.finalproject.domain.dto.*;
import com.likelion.finalproject.service.CommentService;
import com.likelion.finalproject.service.LikeService;
import com.likelion.finalproject.service.PostService;
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
public class PostRestController {

    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    /* 게시글 Post */

    @ApiOperation(value = "게시글 작성")
    @PostMapping
    public Response<PostResponse> post(@RequestBody PostRequest dto, Authentication authentication) {
        String userName = authentication.getName();
        log.info("userName = {}", userName);
        PostResponse postResponse = postService.post(dto, userName);
        return Response.success(postResponse);
    }

    @ApiOperation(value = "게시글 단건 조회 하기")
    @GetMapping("/{postId}")
    public Response<PostReadResponse> getPost(@PathVariable Integer postId) {
        log.info("postId = {}", postId);
        PostReadResponse post = postService.getPost(postId);
        return Response.success(post);
    }

    @ApiOperation(value = "게시글 목록 확인")
    @GetMapping
    public Response<Page<PostReadResponse>> getPostList() {
        PageRequest pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        List<PostReadResponse> post = postService.getAllPost(pageable);
        return Response.success(new PageImpl<>(post));
    }

    @ApiOperation(value = "게시글 수정")
    @PutMapping("/{postId}")
    public Response<PostResponse> modifiedPost(@PathVariable Integer postId,
                                               @RequestBody PostModifyRequest dto,
                                               Authentication authentication) {
        String userName = authentication.getName();
        postService.modifyPost(postId, dto, userName);
        return Response.success(new PostResponse("포스트 수정 완료", postId));
    }

    @ApiOperation(value = "게시글 삭제")
    @DeleteMapping("/{postId}")
    public Response<PostResponse> deletePost(@PathVariable Integer postId,
                                             Authentication authentication) {
        String userName = authentication.getName();
        postService.deletePost(postId, userName);
        return Response.success(new PostResponse("포스트 삭제 완료", postId));
    }

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


    /* 좋아요 Like  */
    @ApiOperation("좋아요 증가")
    @PostMapping("/{postId}/likes")
    public Response<String> IncreaseLike(@PathVariable Integer postId, Authentication authentication) {
        String userName = authentication.getName();
        return Response.success(likeService.increaseLike(postId, userName));
    }
}