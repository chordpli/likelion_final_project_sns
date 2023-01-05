package com.likelion.finalproject.domain.dto.post;

import com.likelion.finalproject.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PostDto {

    private Integer id;
    private String body;
    private String title;
    private User user;
}