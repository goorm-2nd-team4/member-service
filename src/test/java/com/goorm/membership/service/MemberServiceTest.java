package com.goorm.membership.service;

import com.goorm.membership.Model.Member;
import com.goorm.membership.Model.Role;
import com.goorm.membership.dto.SignupRequestDto;
import com.goorm.membership.exception.DuplicateEmailException;
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
        SignupRequestDto request = createRequest();
        Member savedMember = new Member();
        savedMember.setId(1L);
        savedMember.setEmail(request.getEmail());
        savedMember.setPassword(request.getPassword());
        savedMember.setName(request.getName());
        savedMember.setAge(request.getAge());
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
        SignupRequestDto request = createRequest();
        given(memberRepository.existsByEmail("goorm@example.com")).willReturn(true);

        assertThatThrownBy(() -> memberService.signup(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("이미 사용 중인 이메일입니다.");
    }

    private SignupRequestDto createRequest() {
        SignupRequestDto request = new SignupRequestDto();
        request.setEmail("goorm@example.com");
        request.setPassword("password123");
        request.setName("구름");
        request.setAge(20);
        return request;
    }
}
