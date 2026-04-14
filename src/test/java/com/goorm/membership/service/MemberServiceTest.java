package com.goorm.membership.service;

import com.goorm.membership.dto.member.request.LoginRequest;
import com.goorm.membership.dto.member.request.RegisterRequest;
import com.goorm.membership.entity.Member;
import com.goorm.membership.entity.Role;
import com.goorm.membership.exception.DuplicateEmailException;
import com.goorm.membership.exception.InvalidPasswordException;
import com.goorm.membership.exception.MemberNotFoundException;
import com.goorm.membership.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    void signup_savesMember_whenEmailIsAvailable() {
        RegisterRequest request = createRequest();
        Member savedMember = new Member();
        savedMember.setId(1L);
        savedMember.setEmail(request.email());
        savedMember.setPassword(request.password());
        savedMember.setName(request.name());
        savedMember.setRole(Role.USER);

        given(memberRepository.existsByEmail("goorm@example.com")).willReturn(false);
        given(memberRepository.save(any(Member.class))).willReturn(savedMember);

        Member result = memberService.signup(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("goorm@example.com");
        assertThat(result.getRole()).isEqualTo(Role.USER);
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void signup_throwsException_whenEmailIsDuplicated() {
        RegisterRequest request = createRequest();
        given(memberRepository.existsByEmail("goorm@example.com")).willReturn(true);

        assertThatThrownBy(() -> memberService.signup(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("이미 사용 중인 이메일입니다.");
    }

    @Test
    void login_returnsMember_whenCredentialsMatch() {
        LoginRequest request = new LoginRequest("goorm@example.com", "password123");

        Member member = new Member();
        member.setEmail(request.email());
        member.setPassword(request.password());
        member.setRole(Role.USER);

        given(memberRepository.findByEmail("goorm@example.com"))
                .willReturn(java.util.Optional.of(member));

        assertThat(memberService.login(request)).isSameAs(member);
    }

    @Test
    void login_throwsException_whenPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest("goorm@example.com", "wrong-password");

        Member member = new Member();
        member.setEmail("goorm@example.com");
        member.setPassword("password123");
        member.setRole(Role.USER);

        given(memberRepository.findByEmail("goorm@example.com"))
                .willReturn(java.util.Optional.of(member));

        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    @Test
    void login_throwsException_whenMemberDoesNotExist() {
        LoginRequest request = new LoginRequest("missing@example.com", "password123");

        given(memberRepository.findByEmail("missing@example.com"))
                .willReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    private RegisterRequest createRequest() {
        return new RegisterRequest("goorm@example.com", "password123", "password123", "구름");
    }
}
