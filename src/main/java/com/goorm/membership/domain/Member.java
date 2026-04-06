package com.goorm.membership.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotNull
    private Integer age;
}