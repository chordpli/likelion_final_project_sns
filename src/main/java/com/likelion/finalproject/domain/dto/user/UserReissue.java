package com.likelion.finalproject.domain.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserReissue {
    private String accessToken;
    private String refreshToken;
}
