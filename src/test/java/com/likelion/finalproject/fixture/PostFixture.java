package com.likelion.finalproject.fixture;

import com.likelion.finalproject.domain.dto.PostDto;
import com.likelion.finalproject.domain.dto.PostReadResponse;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.domain.enums.UserRole;

import java.time.LocalDateTime;

public class PostFixture {
    public static Post get() {
        User user = UserFixture.get("chordpli", "1234");

        return Post.builder()
                .id(1)
                .user(user)
                .title("title")
                .body("body")
                .build();


    }
}
