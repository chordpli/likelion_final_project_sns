package com.likelion.finalproject.controller;

import com.likelion.finalproject.domain.Response;
import com.likelion.finalproject.domain.dto.UserDto;
import com.likelion.finalproject.domain.dto.UserJoinRequest;
import com.likelion.finalproject.domain.dto.UserJoinResponse;
import com.likelion.finalproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest dto){
        UserDto user = userService.join(dto);
        return Response.success(new UserJoinResponse(user.getId(), user.getUserName()));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(){
        return ResponseEntity.ok().body("ok");
    }
}
