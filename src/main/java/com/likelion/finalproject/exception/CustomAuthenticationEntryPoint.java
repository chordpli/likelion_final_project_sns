package com.likelion.finalproject.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.finalproject.domain.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.likelion.finalproject.exception.ErrorCode.*;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = (String) request.getAttribute("exception");
        ErrorCode errorCode;

        log.debug("log: exception: {} ", exception);

        if (exception.equals(UNKNOWN_ERROR.name())) {
            log.info("알 수 없는 에러가 발생하였습니다.");
            setResponse(response, UNKNOWN_ERROR);

        }
        if (exception.equals(INVALID_TOKEN.name())) {
            log.info("토큰이 만료되었습니다.");
            setResponse(response, INVALID_TOKEN);
        }

        if (exception.equals(INVALID_PERMISSION.name())) {
            log.info("권한이 없습니다.");
            setResponse(response, INVALID_PERMISSION);
        }
    }

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