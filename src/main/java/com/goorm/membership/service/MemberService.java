package com.goorm.membership.service;

import com.goorm.membership.dto.member.request.LoginRequest;
import com.goorm.membership.dto.member.request.RegisterRequest;
import com.goorm.membership.entity.Member;
import com.goorm.membership.entity.Role;
import com.goorm.membership.exception.DuplicateEmailException;
import com.goorm.membership.exception.InvalidPasswordException;
import com.goorm.membership.exception.MemberNotFoundException;
import com.goorm.membership.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* =========================
       회원가입
    ========================= */
    @Transactional
    public Member signup(RegisterRequest request) {

        if (memberRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
        }

        Member member = new Member();
        member.setEmail(request.email());
        member.setPassword(passwordEncoder.encode(request.password()));
        member.setName(request.name());
        member.setRole(Role.USER);

        return memberRepository.save(member);
    }

    /* =========================
       전체 조회
    ========================= */
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    /* =========================
       로그인
    ========================= */
    public Member login(LoginRequest loginRequest) {
        Member member = memberRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(loginRequest.password(), member.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }

    /* =========================
       수정
    ========================= */
    @Transactional
    public Member update(Long id, RegisterRequest request) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("회원 없음"));

        member.setName(request.name());

        return member;
    }

    /* =========================
       삭제
    ========================= */
    @Transactional
    public void delete(Long id) {
        memberRepository.deleteById(id);
    }
}