package com.likelion.finalproject.fixture;

import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.domain.enums.UserRole;

public class UserFixture {
    public static User get(String userName, String password) {
        return User.builder()
                .id(1)
                .userName(userName)
                .password(password)
                .userRole(UserRole.USER)
                .build();
    }
}
