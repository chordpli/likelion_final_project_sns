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

    /**
     * userName을 가지고 있는 user의 Alarm을 확인합니다.
     * @param userName 요청을 보낸 user
     * @param pageable 페이징 정보를 담고 있음.
     * @return
     */
    @Transactional
    public List<AlarmResponse> getMyAlarms(String userName, PageRequest pageable) {
        User user = service.validateGetUserByUserName(userName);

        Page<Alarm> alarms = alarmRepository.findAlarmsByUser(user, pageable);
        return alarms.stream()
                .map(Alarm::toResponse)
                .collect(Collectors.toList());
    }
}
