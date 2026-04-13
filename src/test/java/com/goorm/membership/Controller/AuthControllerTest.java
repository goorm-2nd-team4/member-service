package com.goorm.membership.Controller;

import com.goorm.membership.Model.Member;
import com.goorm.membership.Model.Role;
import com.goorm.membership.dto.LoginMemberDto;
import com.goorm.membership.dto.LoginRequestDto;
import com.goorm.membership.exception.InvalidPasswordException;
import com.goorm.membership.exception.MemberNotFoundException;
import com.goorm.membership.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class AuthControllerTest {

    private MockMvc mockMvc;
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = mock(MemberService.class);
        AuthController authController = new AuthController(memberService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setValidator(validator)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void login_redirectsToLoginSuccess_whenUserCredentialsAreValid() throws Exception {
        Member member = new Member();
        member.setId(1L);
        member.setEmail("user@example.com");
        member.setPassword("password123");
        member.setName("일반회원");
        member.setRole(Role.USER);

        given(memberService.login(any(LoginRequestDto.class))).willReturn(member);

        mockMvc.perform(post("/login")
                        .param("email", "user@example.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login-success"))
                .andExpect(request().sessionAttribute("loginMember", org.hamcrest.Matchers.instanceOf(LoginMemberDto.class)))
                .andExpect(flash().attribute("memberName", "일반회원"));
    }

    @Test
    void login_redirectsToMembers_whenAdminCredentialsAreValid() throws Exception {
        Member member = new Member();
        member.setId(2L);
        member.setEmail("admin@example.com");
        member.setPassword("admin1234");
        member.setName("관리자");
        member.setRole(Role.ADMIN);

        given(memberService.login(any(LoginRequestDto.class))).willReturn(member);

        mockMvc.perform(post("/login")
                        .param("email", "admin@example.com")
                        .param("password", "admin1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members"))
                .andExpect(request().sessionAttribute("loginMember", org.hamcrest.Matchers.instanceOf(LoginMemberDto.class)));
    }

    @Test
    void login_returnsLoginView_whenMemberDoesNotExist() throws Exception {
        given(memberService.login(any(LoginRequestDto.class)))
                .willThrow(new MemberNotFoundException("존재하지 않는 회원입니다."));

        mockMvc.perform(post("/login")
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

        mockMvc.perform(post("/login")
                        .param("email", "user@example.com")
                        .param("password", "wrong-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("loginError", "비밀번호가 일치하지 않습니다."));
    }
}
