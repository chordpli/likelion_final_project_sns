package com.likelion.finalproject.domain.entity;

import com.likelion.finalproject.domain.enums.UserRole;
import lombok.*;

import javax.persistence.*;

@Entity(name = "USERS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends BaseUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "USER_ROLE")
    private UserRole userRole;
    @Column(name = "USER_NAME")
    private String userName;

    public User changeUserRole(UserRole userRole) {
        return User.builder()
                .id(this.id)
                .password(this.password)
                .userRole(userRole)
                .userName(this.userName)
                .build();
    }
}
