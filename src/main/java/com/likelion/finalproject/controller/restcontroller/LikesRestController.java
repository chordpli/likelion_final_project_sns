package com.likelion.finalproject.controller.restcontroller;

import com.likelion.finalproject.domain.Response;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.service.LikesService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/posts")
@RestController
@RequiredArgsConstructor
@Slf4j
public class LikesRestController {

    private final LikesService likeService;

    /* 좋아요 Like  */
    @ApiOperation("좋아요 증가")
    @PostMapping("/{postId}/likes")
    public Response<String> IncreaseLike(@PathVariable Integer postId, Authentication authentication) {
        String userName = authentication.getName();
        return Response.success(likeService.increaseLike(postId, userName));
    }

    @ApiOperation("좋아요 개수 확인")
    @GetMapping("/{postId}/likes")
    public Response<Integer> getLikeCount(@PathVariable Integer postId) {
        return Response.success(likeService.getLikeCount(postId));
    }
}


