package com.likelion.finalproject.domain.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@SQLDelete(sql = "UPDATE likes SET deleted_at = current_timestamp WHERE id = ?")
public class Likes extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = LAZY)
    private User user;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = LAZY)
    private Post post;

    public static Likes toEntity(Post post, User user) {
        return Likes.builder()
                .user(user)
                .post(post)
                .build();
    }
}
