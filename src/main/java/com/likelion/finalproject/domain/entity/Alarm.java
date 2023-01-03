package com.likelion.finalproject.domain.entity;

import com.likelion.finalproject.domain.enums.AlarmArgs;
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
public class Alarm extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 알림을 받는 사람 입니다.
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType; // like인지 comment인지

    private Integer fromUserId;
    private Integer targetId; // podst id
    private String text;

}
