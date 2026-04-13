package com.goorm.membership.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {

    @NotBlank(message = "이메일은 필수값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수값입니다.")
    private String password;

    @NotBlank(message = "이름은 필수값입니다.")
    private String name;

    @NotNull(message = "나이는 필수값입니다.")
    @Min(value = 0, message = "나이는 0 이상이어야 합니다.")
    private Integer age;
}
