package com.likelion.finalproject.domain.dto.post;

import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostModifyRequest {
    private String title;
    private String body;

    public Post toEntity(Integer postId, User user) {
        return Post.builder()
                .id(postId)
                .title(this.title)
                .body(this.body)
                .user(user)
                .build();
    }
}
