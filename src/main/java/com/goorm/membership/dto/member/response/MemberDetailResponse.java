package com.goorm.membership.dto.member.response;

import com.goorm.membership.entity.Member;
import com.goorm.membership.entity.Role;

public record MemberDetailResponse(
        Long id,
        String email,
        String name,
        Role role
) {
    public static MemberDetailResponse from(Member member) {
        return new MemberDetailResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getRole()
        );
    }
}
