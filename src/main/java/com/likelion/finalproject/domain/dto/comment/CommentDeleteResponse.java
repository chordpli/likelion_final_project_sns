package com.likelion.finalproject.domain.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CommentDeleteResponse {
    private String message;
    private Integer id;
}
