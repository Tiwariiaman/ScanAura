package com.scanaura.auth.entity;

import com.scanaura.common.entity.BaseEntity;
import com.scanaura.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String mobile;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean deleted = false;

}
