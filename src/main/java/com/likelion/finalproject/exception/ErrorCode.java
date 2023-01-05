package com.likelion.finalproject.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ErrorCode {
    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자가 존재하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "패스워드가 잘못되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "사용자가 권한이 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 포스트가 존재하지 않습니다.."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB에러"),
    NOT_EXIST_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다."),
    UNKNOWN_ERROR(HttpStatus.BAD_REQUEST, "알 수 없는 에러가 발생하였습니다."),
    MISMATCH_USER(HttpStatus.UNAUTHORIZED, "사용자가 일치하지 않습니다."),
    MISMATCH_COMMENT(HttpStatus.UNAUTHORIZED, "댓글이 일치하지 않습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글이 존재하지 않습니다." ),
    NOT_LOGGED_IN(HttpStatus.CONFLICT, "로그인 상태가 아닙니다.");

    private HttpStatus status;
    private String message;
}
