package com.goorm.membership.dto.member.response;

import com.goorm.membership.entity.Member;
import com.goorm.membership.entity.Role;

public record LoginResponse(
        Long id,
        String email,
        String name,
        Role role
) {
    public static LoginResponse from(Member member) {
        return new LoginResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getRole()
        );
    }
}
