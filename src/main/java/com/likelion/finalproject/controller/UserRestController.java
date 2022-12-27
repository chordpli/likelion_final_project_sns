package com.likelion.finalproject.controller;

import com.likelion.finalproject.domain.Response;
import com.likelion.finalproject.domain.dto.*;
import com.likelion.finalproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserRestController {

    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest dto){
        UserDto user = userService.join(dto);
        return Response.success(new UserJoinResponse(user.getId(), user.getUserName()));
    }

    @PostMapping("/{userId}/role/change")
    public Response<UserSwithResponse> switchToAdmin(@PathVariable Integer userId, Authentication authentication){
        log.info("toAdmin userId ={}", userId);
        UserSwithResponse user = userService.toAdmin(userId, authentication.getName());
        log.info("toAdmin user ={}", user.getUserRole());
        return Response.success(user);
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest dto){
        UserLoginResponse loginUser = userService.login(dto);
        return Response.success(loginUser);
    }
}
