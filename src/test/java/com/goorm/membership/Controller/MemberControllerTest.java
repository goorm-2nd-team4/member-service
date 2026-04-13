package com.goorm.membership.Controller;

import com.goorm.membership.Model.Member;
import com.goorm.membership.dto.SignupRequestDto;
import com.goorm.membership.exception.DuplicateEmailException;
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
                        .param("name", "구름")
                        .param("age", "20"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message", "회원가입이 완료되었습니다."));
    }

    @Test
    void save_returnsForm_whenValidationFails() throws Exception {
        mockMvc.perform(post("/members/save")
                        .param("email", "not-an-email")
                        .param("password", "")
                        .param("name", "")
                        .param("age", "-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("new-form"))
                .andExpect(model().attributeHasFieldErrors(
                        "signupRequest",
                        "email",
                        "password",
                        "name",
                        "age"
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
                        .param("name", "구름")
                        .param("age", "20"))
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
        member.setAge(20);

        given(memberService.findAll()).willReturn(List.of(member));

        mockMvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andExpect(view().name("members"))
                .andExpect(model().attributeExists("members"));
    }
}
