package com.likelion.finalproject.domain.entity;

import com.likelion.finalproject.domain.dto.AlarmResponse;
import com.likelion.finalproject.domain.enums.AlarmType;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
public class Alarm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 알림을 받는 사람.
     */
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType; // like인지 comment인지

    /**
     * 알림을 발생시킨 userID
     */
    private Integer fromUserId;

    /**
     * 알림이 발생된 postID
     */
    private Integer targetId; // podst id

    private Integer commentId;

    private String text;

    public static Alarm toEntity(User user, Post post, AlarmType alarmType) {
        return Alarm.builder()
                .user(post.getUser())
                .alarmType(alarmType)
                .fromUserId(user.getId())
                .targetId(post.getId())
                .text(alarmType.getText())
                .build();
    }

    public static Alarm toEntity(User user, Post post, AlarmType alarmType, Integer commentId) {
        return Alarm.builder()
                .user(post.getUser())
                .alarmType(alarmType)
                .fromUserId(user.getId())
                .targetId(post.getId())
                .text(alarmType.getText())
                .commentId(commentId)
                .build();
    }

    public AlarmResponse toResponse() {
        return AlarmResponse.builder()
                .id(this.id)
                .alarmType(this.alarmType)
                .fromUserId(this.fromUserId)
                .targetId(this.targetId)
                .text(this.alarmType.getText())
                .createdAt(this.getCreatedAt())
                .build();
    }

    @Override
    public void cancelDeletion() {
        super.cancelDeletion();
    }
}
