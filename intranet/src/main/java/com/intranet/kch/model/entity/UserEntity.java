package com.intranet.kch.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class UserEntity extends Base {
    @Id @GeneratedValue
    private Long id;
    private String loginId;
    private String password;
    private String name;
    private String email;
    private String phone;

    public UserEntity(String loginId, String password, String name, String email, String phone) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public UserEntity() {
    }
}
