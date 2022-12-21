package com.likelion.finalproject.exception;

import com.likelion.finalproject.domain.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionManager {

    @ExceptionHandler(SNSAppException.class)
    public ResponseEntity<?> SNSAppExceptionHandler(SNSAppException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("errorCode", e.getErrorCode());
        result.put("message", e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error("ERROR", result));
    }
}
