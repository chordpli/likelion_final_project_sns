package com.likelion.finalproject.domain.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity(name = "LIKES")
@SQLDelete(sql = "UPDATE likes SET deleted_at = current_timestamp WHERE id = ?")
public class Like extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = LAZY)
    private User user;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = LAZY)
    private Post post;

    @Override
    public void cancelDeletion() {
        super.cancelDeletion();
    }

    public static Like toEntity(Post post, User user) {
        return Like.builder()
                .user(user)
                .post(post)
                .build();
    }
}
