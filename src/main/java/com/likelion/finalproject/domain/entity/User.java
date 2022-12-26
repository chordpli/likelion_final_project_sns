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
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "USER_ROLE")
    private UserRole userRole;
    @Column(name = "USER_NAME")
    private String userName;

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
