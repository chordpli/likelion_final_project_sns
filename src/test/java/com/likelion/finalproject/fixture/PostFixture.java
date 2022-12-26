package com.likelion.finalproject.fixture;

import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;

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

    public static Post get(User user) {
        return Post.builder()
                .id(1)
                .user(user)
                .title("title")
                .body("body")
                .build();
    }
}
