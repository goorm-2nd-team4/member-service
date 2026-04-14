package com.goorm.membership.dto.member.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,

        @NotBlank(message = "비밀번호 확인은 필수입니다.")
        String confirmPassword,

        @NotBlank(message = "이름은 필수입니다.")
        String name
) {
    @AssertTrue(message = "비밀번호가 일치하지 않습니다.")
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }
}
