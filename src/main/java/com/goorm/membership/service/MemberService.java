package com.goorm.membership.service;

import com.goorm.membership.Model.Member;
import com.goorm.membership.Model.Role;
import com.goorm.membership.dto.SignupRequestDto;
import com.goorm.membership.exception.DuplicateEmailException;
import com.goorm.membership.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member signup(SignupRequestDto requestDto) {
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
        }

        Member member = new Member();
        member.setEmail(requestDto.getEmail());
        member.setPassword(requestDto.getPassword());
        member.setName(requestDto.getName());
        member.setAge(requestDto.getAge());
        member.setRole(Role.USER);

        return memberRepository.save(member);
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }
}
