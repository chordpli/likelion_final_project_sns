package com.likelion.finalproject.controller.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.finalproject.service.LikesService;
import com.likelion.finalproject.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

@WebMvcTest(LikesRestController.class)
@WithMockUser
class LikesRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LikesService likeService;

    private String token;
    @Value("${jwt.secret}")
    private String secretKey;

    public final LocalDateTime time = LocalDateTime.now();

    @BeforeEach()
    public void getToken() {
        long expireTimeMs = 1000 * 60 * 60;
        token = JwtUtil.createJwt("chordpli", secretKey, System.currentTimeMillis() + expireTimeMs);
    }

}