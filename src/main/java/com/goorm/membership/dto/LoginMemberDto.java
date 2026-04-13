package com.goorm.membership.dto;

import com.goorm.membership.Model.Member;
import com.goorm.membership.Model.Role;
import lombok.Getter;

@Getter
public class LoginMemberDto {

    private final Long id;
    private final String email;
    private final String name;
    private final Role role;

    public LoginMemberDto(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.role = member.getRole();
    }
}
