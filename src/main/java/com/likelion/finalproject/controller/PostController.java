package com.likelion.finalproject.controller;

import com.likelion.finalproject.domain.Response;
import com.likelion.finalproject.domain.dto.PostReadResponse;
import com.likelion.finalproject.domain.dto.PostRequest;
import com.likelion.finalproject.domain.dto.PostResponse;
import com.likelion.finalproject.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/posts")
@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<PostResponse> post(@RequestBody PostRequest dto, Authentication authentication){
        String userName = authentication.getName();
        log.info("userName = {}", userName);
        PostResponse postResponse = postService.post(dto, userName);
        return Response.success(postResponse);
    }

    @GetMapping("/{postId}")
    public Response<PostReadResponse> post(@PathVariable Integer postId){
        log.info("postId = {}", postId);
        PostReadResponse post = postService.getPost(postId);
        return Response.success(post);
    }
}