package com.likelion.finalproject.domain.entity;

import com.likelion.finalproject.domain.enums.AlarmArgs;
import com.likelion.finalproject.domain.enums.AlarmType;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
    private Integer id;

    private AlarmType alarmType;

    private AlarmArgs args;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = LAZY)
    private User user;
}
