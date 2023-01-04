package com.likelion.finalproject.repository;

import com.likelion.finalproject.domain.dto.AlarmResponse;
import com.likelion.finalproject.domain.entity.Alarm;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Integer> {
    Optional<Alarm> findAlarmByFromUserIdAndTargetId(Integer fromUserId, Integer targetId);

    Page<Alarm> findAlarmsByUser(User user, Pageable pageable);
}
