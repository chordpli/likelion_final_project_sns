package com.likelion.finalproject.domain.dto.user;

import com.likelion.finalproject.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.likelion.finalproject.domain.enums.UserRole.USER;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
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
