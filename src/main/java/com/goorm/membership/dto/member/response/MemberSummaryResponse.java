package com.goorm.membership.dto.member.response;

import com.goorm.membership.entity.Member;
import com.goorm.membership.entity.Role;

public record MemberSummaryResponse(
        Long id,
        String email,
        String name,
        Role role
) {
    public static MemberSummaryResponse from(Member member) {
        return new MemberSummaryResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getRole()
        );
    }
}
