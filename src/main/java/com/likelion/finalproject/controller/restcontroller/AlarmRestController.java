package com.likelion.finalproject.controller.restcontroller;

import com.likelion.finalproject.domain.Response;
import com.likelion.finalproject.domain.dto.AlarmResponse;
import com.likelion.finalproject.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/alarms")
public class AlarmRestController {

    private final AlarmService alarmService;

    @GetMapping
    public Response<Page<AlarmResponse>> getAlarmsByUser(Authentication authentication) {
        String userName = authentication.getName();
        PageRequest pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        List<AlarmResponse> myAlarms = alarmService.getMyAlarms(userName, pageable);
        return Response.success(new PageImpl<>(myAlarms));
    }
}
