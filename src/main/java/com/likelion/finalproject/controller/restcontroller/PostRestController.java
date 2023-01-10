package com.likelion.finalproject.controller.restcontroller;

import com.likelion.finalproject.domain.Response;
import com.likelion.finalproject.domain.dto.post.PostModifyRequest;
import com.likelion.finalproject.domain.dto.post.PostReadResponse;
import com.likelion.finalproject.domain.dto.post.PostRequest;
import com.likelion.finalproject.domain.dto.post.PostResponse;
import com.likelion.finalproject.service.PostService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/posts")
@RestController
@RequiredArgsConstructor
@Slf4j
public class PostRestController {

    private final PostService postService;

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

    /* 마이피드 */
    @ApiOperation("마이 피드 조회")
    @GetMapping("/my")
    public Response<Page<PostReadResponse>> getMyFeed(Authentication authentication,
                                                      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String userName = authentication.getName();
        //PageRequest pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        Page<PostReadResponse> myFeeds = postService.getMyAllPost(userName, pageable);
        return Response.success(myFeeds);
    }
}