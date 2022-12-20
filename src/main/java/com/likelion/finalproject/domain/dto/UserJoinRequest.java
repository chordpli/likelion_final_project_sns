package com.likelion.finalproject.domain.dto;

import com.likelion.finalproject.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.likelion.finalproject.domain.UserRole.USER;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserJoinRequest {
    private String userName;
    private String password;

    public User toEntity(String password) {
        return User.builder()
                .userName(this.userName)
                .password(password)
                .userRole(USER)
                .build();
    }
}
