package com.likelion.finalproject.controller.restcontroller;

import com.likelion.finalproject.domain.Response;
import com.likelion.finalproject.service.LikeService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/posts")
@RestController
@RequiredArgsConstructor
@Slf4j
public class LikeRestController {

    private final LikeService likeService;

    /* 좋아요 Like  */
    @ApiOperation("좋아요 증가")
    @PostMapping("/{postId}/likes")
    public Response<String> IncreaseLike(@PathVariable Integer postId, Authentication authentication) {
        String userName = authentication.getName();
        return Response.success(likeService.increaseLike(postId, userName));
    }
}


