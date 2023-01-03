package com.likelion.finalproject.domain.dto;


import com.likelion.finalproject.domain.entity.Comment;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CommentRequest {
    private String comment;

    public Comment toEntity(Integer id, User user, Post post) {
        System.out.println("toentity = " + post.getCreatedAt());
        return Comment.builder()
                .id(id)
                .comment(this.comment)
                .user(user)
                .post(post)
                .build();
    }
}
