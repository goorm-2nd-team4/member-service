package com.goorm.membership.dto.member.response;

import com.goorm.membership.entity.Member;
import com.goorm.membership.entity.Role;

public record LoginResponse(
        Long id,
        String email,
        String name,
        Role role,
        String token
) {
    public static LoginResponse from(Member member, String token) {
        return new LoginResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getRole(),
                token
        );
    }
}
