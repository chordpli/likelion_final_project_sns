package com.likelion.finalproject.domain.dto;

import com.likelion.finalproject.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
public class UserDto {
    private Integer id;
    private String password;
    private UserRole userRole;
    private String userName;
}
