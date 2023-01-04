package com.likelion.finalproject.domain.entity;

import com.likelion.finalproject.domain.dto.PostReadResponse;
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
@SQLDelete(sql = "UPDATE post SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is null")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String body;
    private String title;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public PostReadResponse toResponse() {
        return PostReadResponse.builder()
                .id(this.getId())
                .title(this.getTitle())
                .body(this.getBody())
                .userName(this.getUser().getUserName())
                .createdAt(this.getCreatedAt())
                .lastModifiedAt(this.getLastModifiedAt())
                .build();
    }
}