package com.likelion.finalproject.controller;

import com.likelion.finalproject.domain.Response;
import com.likelion.finalproject.domain.dto.*;
import com.likelion.finalproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/join")
    public String joinPage(Model model){
        log.info("join controller getmapping");
        return "users/register";
    }
    @PostMapping("/join")
    public String join(@RequestParam("userName") String userName,
                       @RequestParam("password") String password){
        log.info("join controller name ={}, passowrd ={}", userName, password);
        UserJoinRequest request = new UserJoinRequest(userName, password);
        UserDto user = userService.join(request);
        return "redirect:/";
    }

    @PostMapping("/{userId}/role/change")
    public Response<UserSwithResponse> switchToAdmin(@PathVariable Integer userId, Authentication authentication){
        log.info("toAdmin userId ={}", userId);
        UserSwithResponse user = userService.toAdmin(userId, authentication.getName());
        log.info("toAdmin user ={}", user.getUserRole());
        return Response.success(user);
    }

    @GetMapping("/login")
    public String loginPage(){
        return "users/signin";
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest dto){
        UserLoginResponse loginUser = userService.login(dto);
        return Response.success(loginUser);
    }
}
