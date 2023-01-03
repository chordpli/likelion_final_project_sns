package com.likelion.finalproject.fixture;

import com.likelion.finalproject.domain.entity.Comment;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;

public class CommentFixture {
    public static Comment get(User user, Post post) {
        return Comment.builder()
                .id(1)
                .comment("댓글")
                .user(user)
                .post(post)
                .build();
    }

    public static Comment get() {
        User user = UserFixture.get("chordpli", "1234");
        Post post = PostFixture.get(user);
        return Comment.builder()
                .id(1)
                .comment("댓글")
                .user(user)
                .post(post)
                .build();
    }
}
