package com.likelion.finalproject.controller;

import com.likelion.finalproject.domain.Response;
import com.likelion.finalproject.domain.dto.user.UserJoinRequest;
import com.likelion.finalproject.domain.dto.user.UserLoginRequest;
import com.likelion.finalproject.domain.dto.user.UserLoginResponse;
import com.likelion.finalproject.domain.dto.user.UserSwithResponse;
import com.likelion.finalproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/join")
    public String joinPage(Model model) {
        log.info("join controller getmapping");
        return "users/register";
    }

    @PostMapping("/join")
    public String join(@ModelAttribute UserJoinRequest request) {
        log.info("join controller name ={}, passowrd ={}", request.getUserName(), request.getPassword());
        userService.join(request);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "users/signin";
    }

    @PostMapping("/login")
    public String login(@RequestBody UserLoginRequest dto) {
        UserLoginResponse loginUser = userService.login(dto);
        return "redirect:/";
    }

    @PostMapping("/{userId}/role/change")
    public Response<UserSwithResponse> changeUserRoleToAdmin(@PathVariable Integer userId, Authentication authentication) {
        log.info("toAdmin userId ={}", userId);
        UserSwithResponse user = userService.changeUserRoleToAdmin(userId, authentication.getName());
        log.info("toAdmin user ={}", user.getUserRole());
        return Response.success(user);
    }
}
