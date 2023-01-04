package com.likelion.finalproject.repository;

import com.likelion.finalproject.domain.entity.Alarm;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.domain.enums.AlarmType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Integer> {
    Optional<Alarm> findAlarmByFromUserIdAndTargetIdAndAlarmType(Integer fromUserId, Integer targetId, AlarmType alarmType);

    Page<Alarm> findAlarmsByUser(User user, Pageable pageable);

}
