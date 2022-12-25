package com.likelion.finalproject.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.finalproject.domain.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = (String) request.getAttribute("exception");
        ErrorCode errorCode;

        log.debug("log: exception: {} ", exception);

        if (exception == null) {
            log.info("토큰이 존재하지 않습니다.");
            setResponse(response, ErrorCode.NOT_EXIST_TOKEN);

        }
        if (exception.equals(ErrorCode.INVALID_TOKEN.name())) {
            log.info("토큰이 만료되었습니다.");
            setResponse(response, ErrorCode.INVALID_TOKEN);
        }

        if (exception.equals(ErrorCode.INVALID_PERMISSION.name())) {
            log.info("권한이 없습니다.");
            setResponse(response, ErrorCode.INVALID_PERMISSION);
        }
    }

    /**
     * 한글 출력을 위해 getWriter() 사용
     */
    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getStatus().value());

        Map<String, Object> result = new HashMap<>();
        result.put("errorCode", errorCode.name());
        result.put("message", errorCode.getMessage());

        response.getWriter().println(
                objectMapper.writeValueAsString(
                        Response.error("ERROR", result)));

    }
}