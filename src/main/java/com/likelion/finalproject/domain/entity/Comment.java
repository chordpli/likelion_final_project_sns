package com.likelion.finalproject.domain.entity;

import com.likelion.finalproject.domain.dto.comment.CommentReadResponse;
import com.likelion.finalproject.domain.dto.comment.CommentRequest;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@SQLDelete(sql = "UPDATE comment SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is null")
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String comment;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = LAZY)
    private User user;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = LAZY)
    private Post post;

    public static Comment toEntity(User user, Post post, CommentRequest request) {
        return Comment.builder()
                .user(user)
                .post(post)
                .comment(request.getComment())
                .build();
    }

    /**
     * 기존 코멘트 내용을 comment로 수정합니다.
     * @param comment 수정할 comment 문자열
     */
    public void update(String comment) {
        this.comment = comment;
    }

    public CommentReadResponse toResponse() {
        return CommentReadResponse.builder()
                .id(this.id)
                .comment(this.comment)
                .userName(this.user.getUserName())
                .postId(this.post.getId())
                .createdAt(this.getCreatedAt())
                .build();
    }
}
