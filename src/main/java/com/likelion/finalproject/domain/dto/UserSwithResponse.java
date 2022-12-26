package com.likelion.finalproject.domain.dto;

import com.likelion.finalproject.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserSwithResponse {

    private String userName;
    private UserRole userRole;
}
