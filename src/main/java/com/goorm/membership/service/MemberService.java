package com.goorm.membership.service;

import com.goorm.membership.entity.Member;
import com.goorm.membership.entity.Role;
import com.goorm.membership.dto.LoginRequestDto;
import com.goorm.membership.dto.SignupRequestDto;
import com.goorm.membership.dto.member.request.LoginRequest;
import com.goorm.membership.dto.member.request.RegisterRequest;
import com.goorm.membership.exception.DuplicateEmailException;
import com.goorm.membership.exception.InvalidPasswordException;
import com.goorm.membership.exception.MemberNotFoundException;
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
        return signup(requestDto.getEmail(), requestDto.getPassword(), requestDto.getName());
    }

    @Transactional
    public Member signup(RegisterRequest request) {
        return signup(request.email(), request.password(), request.name());
    }

    private Member signup(String email, String password, String name) {
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
        }

        Member member = new Member();
        member.setEmail(email);
        member.setPassword(password);
        member.setName(name);
        member.setRole(Role.USER);

        return memberRepository.save(member);
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
    }

    public Member login(LoginRequestDto loginRequestDto) {
        return login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
    }

    public Member login(LoginRequest loginRequest) {
        return login(loginRequest.email(), loginRequest.password());
    }

    private Member login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        if (!member.getPassword().equals(password)) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }
}
