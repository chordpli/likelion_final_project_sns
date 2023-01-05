package com.likelion.finalproject.controller.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.finalproject.domain.dto.AlarmResponse;
import com.likelion.finalproject.domain.entity.Alarm;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.fixture.PostFixture;
import com.likelion.finalproject.fixture.UserFixture;
import com.likelion.finalproject.service.AlarmService;
import com.likelion.finalproject.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.likelion.finalproject.domain.enums.AlarmType.NEW_COMMENT_ON_POST;
import static com.likelion.finalproject.domain.enums.AlarmType.NEW_LIKE_ON_POST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlarmRestController.class)
@WithMockUser
class AlarmRestControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AlarmService alarmService;

    private String token;
    @Value("${jwt.secret}")
    private String secretKey;

    public final LocalDateTime time = LocalDateTime.now();

    @BeforeEach()
    public void getToken() {
        long expireTimeMs = 1000 * 60 * 60;
        token = JwtUtil.createJwt("chordpli", secretKey, System.currentTimeMillis() + expireTimeMs);
    }

    @Test
    @DisplayName("알람 목록 조회 성공")
    void success_get_alarm_list() throws Exception {
        User user = UserFixture.get("chordpli", "1234");
        Post post = PostFixture.get(user);

        //List<AlarmResponse> alarms = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("createdAt").descending());

        Alarm alarm1 = Alarm.builder()
                .id(1)
                .alarmType(NEW_COMMENT_ON_POST)
                .user(user)
                .fromUserId(3)
                .targetId(post.getId())
                .text(NEW_COMMENT_ON_POST.getText())
                .build();

        Alarm alarm2 = Alarm.builder()
                .id(2)
                .alarmType(NEW_LIKE_ON_POST)
                .user(user)
                .fromUserId(3)
                .targetId(post.getId())
                .text(NEW_LIKE_ON_POST.getText())
                .build();

        List<AlarmResponse> alarms = Arrays.asList(alarm1.toResponse(), alarm2.toResponse());

        given(alarmService.getMyAlarms(any(), any())).willReturn(alarms);

        String url = "/api/v1/alarms";

        mockMvc.perform(get(url).with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(pageRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].id").value(alarm1.getId()))
                .andExpect(jsonPath("$.result.content[0].alarmType").value(alarm1.getAlarmType().name()))
                .andExpect(jsonPath("$.result.content[0].fromUserId").value(alarm1.getFromUserId()))
                .andExpect(jsonPath("$.result.content[0].targetId").value(alarm1.getTargetId()))
                .andExpect(jsonPath("$.result.content[0].text").value(alarm1.getAlarmType().getText()))
                .andDo(print());
    }
}