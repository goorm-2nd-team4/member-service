package com.goorm.membership.Controller;

import com.goorm.membership.dto.SignupRequestDto;
import com.goorm.membership.exception.DuplicateEmailException;
import com.goorm.membership.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class SignupControllerTest {

    private MockMvc mockMvc;
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = mock(MemberService.class);
        SignupController signupController = new SignupController(memberService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(signupController)
                .setValidator(validator)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void save_redirectsToHome_whenRequestIsValid() throws Exception {
        mockMvc.perform(post("/save")
                        .param("email", "goorm@example.com")
                        .param("password", "password123")
                        .param("name", "구름"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message", "회원가입이 완료되었습니다."));
    }

    @Test
    void save_returnsForm_whenValidationFails() throws Exception {
        mockMvc.perform(post("/save")
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

        mockMvc.perform(post("/save")
                        .param("email", "goorm@example.com")
                        .param("password", "password123")
                        .param("name", "구름"))
                .andExpect(status().isOk())
                .andExpect(view().name("new-form"))
                .andExpect(model().attributeHasFieldErrors("signupRequest", "email"));
    }
}
