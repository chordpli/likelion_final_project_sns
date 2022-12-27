package com.likelion.finalproject.controller;

import com.likelion.finalproject.domain.Response;
import com.likelion.finalproject.domain.dto.PostModifyRequest;
import com.likelion.finalproject.domain.dto.PostReadResponse;
import com.likelion.finalproject.domain.dto.PostRequest;
import com.likelion.finalproject.domain.dto.PostResponse;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.service.PostService;
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
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<PostResponse> post(@RequestBody PostRequest dto, Authentication authentication) {
        String userName = authentication.getName();
        log.info("userName = {}", userName);
        PostResponse postResponse = postService.post(dto, userName);
        return Response.success(postResponse);
    }

    @GetMapping("/{postId}")
    public Response<PostReadResponse> getPost(@PathVariable Integer postId) {
        log.info("postId = {}", postId);
        PostReadResponse post = postService.getPost(postId);
        return Response.success(post);
    }

    @GetMapping
    public Response<Page<PostReadResponse>> getPostList() {
        PageRequest pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        List<PostReadResponse> post = postService.getAllPost(pageable);
        return Response.success(new PageImpl<>(post));
    }

    @PutMapping("/{postId}")
    public Response<PostResponse> modifiedPost(@PathVariable Integer postId,
                                                       @RequestBody PostModifyRequest dto,
                                                       Authentication authentication) {
        String userName = authentication.getName();
        Post post = postService.modifyPost(postId, dto, userName);
        return Response.success(new PostResponse("포스트 수정 완료", post.getId()));
    }

    @DeleteMapping("/{postId}")
    public Response<PostResponse> deletePost(@PathVariable Integer postId,
                                             Authentication authentication){
        String userName = authentication.getName();
        postService.deletePost(postId, userName);
        return Response.success(new PostResponse("포스트 삭제 완료", postId));
    }

}