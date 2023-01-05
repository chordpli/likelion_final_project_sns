package com.likelion.finalproject.domain.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.finalproject.domain.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
public class CommentWriteResponse {

    private Integer id;
    private String comment;
    private String userName;
    private Integer postId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public static CommentWriteResponse of(Comment savedComment) {
        return CommentWriteResponse.builder().
                id(savedComment.getId())
                .comment(savedComment.getComment())
                .userName(savedComment.getUser().getUserName())
                .postId(savedComment.getPost().getId())
                .createdAt(savedComment.getCreatedAt())
                .build();
    }
}
