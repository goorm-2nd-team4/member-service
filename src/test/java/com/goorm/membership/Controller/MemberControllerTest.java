package com.goorm.membership.Controller;

import com.goorm.membership.Model.Member;
import com.goorm.membership.Model.Role;
import com.goorm.membership.dto.LoginRequestDto;
import com.goorm.membership.dto.SignupRequestDto;
import com.goorm.membership.exception.DuplicateEmailException;
import com.goorm.membership.exception.InvalidPasswordException;
import com.goorm.membership.exception.MemberNotFoundException;
import com.goorm.membership.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class MemberControllerTest {

    private MockMvc mockMvc;
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = mock(MemberService.class);
        MemberController memberController = new MemberController(memberService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(memberController)
                .setValidator(validator)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void save_redirectsToHome_whenRequestIsValid() throws Exception {
        mockMvc.perform(post("/members/save")
                        .param("email", "goorm@example.com")
                        .param("password", "password123")
                        .param("name", "구름"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message", "회원가입이 완료되었습니다."));
    }

    @Test
    void login_redirectsToLoginSuccess_whenUserCredentialsAreValid() throws Exception {
        Member member = new Member();
        member.setEmail("user@example.com");
        member.setPassword("password123");
        member.setName("일반회원");
        member.setRole(Role.USER);

        given(memberService.login(any(LoginRequestDto.class))).willReturn(member);

        mockMvc.perform(post("/members/login")
                        .param("email", "user@example.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login-success"))
                .andExpect(flash().attribute("memberName", "일반회원"));
    }

    @Test
    void login_redirectsToMembers_whenAdminCredentialsAreValid() throws Exception {
        Member member = new Member();
        member.setEmail("admin@example.com");
        member.setPassword("admin1234");
        member.setName("관리자");
        member.setRole(Role.ADMIN);

        given(memberService.login(any(LoginRequestDto.class))).willReturn(member);

        mockMvc.perform(post("/members/login")
                        .param("email", "admin@example.com")
                        .param("password", "admin1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members"));
    }

    @Test
    void login_returnsLoginView_whenMemberDoesNotExist() throws Exception {
        given(memberService.login(any(LoginRequestDto.class)))
                .willThrow(new MemberNotFoundException("존재하지 않는 회원입니다."));

        mockMvc.perform(post("/members/login")
                        .param("email", "user@example.com")
                        .param("password", "wrong-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("loginError", "존재하지 않는 회원입니다."));
    }

    @Test
    void login_returnsLoginView_whenPasswordDoesNotMatch() throws Exception {
        given(memberService.login(any(LoginRequestDto.class)))
                .willThrow(new InvalidPasswordException("비밀번호가 일치하지 않습니다."));

        mockMvc.perform(post("/members/login")
                        .param("email", "user@example.com")
                        .param("password", "wrong-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("loginError", "비밀번호가 일치하지 않습니다."));
    }

    @Test
    void save_returnsForm_whenValidationFails() throws Exception {
        mockMvc.perform(post("/members/save")
                        .param("email", "not-an-email")
                        .param("password", "")
                        .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("new-form"))
                .andExpect(model().attributeHasFieldErrors(
                        "signupRequest",
                        "email",
                        "password",
                        "name"
                ));
    }

    @Test
    void save_returnsForm_whenEmailIsDuplicated() throws Exception {
        doThrow(new DuplicateEmailException("이미 사용 중인 이메일입니다."))
                .when(memberService)
                .signup(any(SignupRequestDto.class));

        mockMvc.perform(post("/members/save")
                        .param("email", "goorm@example.com")
                        .param("password", "password123")
                        .param("name", "구름"))
                .andExpect(status().isOk())
                .andExpect(view().name("new-form"))
                .andExpect(model().attributeHasFieldErrors("signupRequest", "email"));
    }

    @Test
    void members_loadsMemberList() throws Exception {
        Member member = new Member();
        member.setId(1L);
        member.setEmail("goorm@example.com");
        member.setName("구름");

        given(memberService.findAll()).willReturn(List.of(member));

        mockMvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andExpect(view().name("members"))
                .andExpect(model().attributeExists("members"));
    }
}
