package com.likelion.finalproject.service;

import com.likelion.finalproject.domain.dto.AlarmResponse;
import com.likelion.finalproject.domain.entity.Alarm;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final ValidateService service;

    @Transactional
    public List<AlarmResponse> getMyAlarms(String userName, PageRequest pageable) {
        User user = service.validateGetUserByUserName(userName);

        Page<Alarm> alarms = alarmRepository.findAlarmsByUser(user, pageable);
        return alarms.stream()
                .map(Alarm::toResponse)
                .collect(Collectors.toList());
    }
}
