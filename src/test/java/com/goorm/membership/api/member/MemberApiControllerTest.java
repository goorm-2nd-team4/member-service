package com.goorm.membership.api.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorm.membership.entity.Member;
import com.goorm.membership.entity.Role;
import com.goorm.membership.dto.member.request.LoginRequest;
import com.goorm.membership.dto.member.request.RegisterRequest;
import com.goorm.membership.exception.DuplicateEmailException;
import com.goorm.membership.exception.GlobalExceptionHandler;
import com.goorm.membership.exception.InvalidPasswordException;
import com.goorm.membership.exception.MemberNotFoundException;
import com.goorm.membership.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberApiControllerTest {

    private MockMvc mockMvc;
    private MemberService memberService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        memberService = mock(MemberService.class);
        MemberApiController memberApiController = new MemberApiController(memberService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(memberApiController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void register_returnsCreatedResponse() throws Exception {
        Member member = createMember(1L, "goorm@example.com", "구름", Role.USER);
        given(memberService.signup(any(RegisterRequest.class))).willReturn(member);

        RegisterRequest request = new RegisterRequest(
                "goorm@example.com",
                "password123",
                "password123",
                "구름"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("회원가입 성공"))
                .andExpect(jsonPath("$.data.email").value("goorm@example.com"))
                .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    void login_returnsUnauthorized_whenPasswordDoesNotMatch() throws Exception {
        given(memberService.login(any(LoginRequest.class)))
                .willThrow(new InvalidPasswordException("비밀번호가 일치하지 않습니다."));

        LoginRequest request = new LoginRequest("goorm@example.com", "wrong-password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void register_returnsBadRequest_whenValidationFails() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "not-an-email",
                "password123",
                "different-password",
                ""
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."))
                .andExpect(jsonPath("$.data.email").value("형식이 올바르지 않습니다."))
                .andExpect(jsonPath("$.data.name").value("이름은 필수값입니다."));
    }

    @Test
    void getMembers_returnsMemberList() throws Exception {
        given(memberService.findAll()).willReturn(List.of(
                createMember(1L, "goorm@example.com", "구름", Role.USER),
                createMember(2L, "admin@example.com", "관리자", Role.ADMIN)
        ));

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원 목록 조회 성공"))
                .andExpect(jsonPath("$.data[0].email").value("goorm@example.com"))
                .andExpect(jsonPath("$.data[1].role").value("ADMIN"));
    }

    @Test
    void getMember_returnsNotFound_whenMemberDoesNotExist() throws Exception {
        given(memberService.findById(99L))
                .willThrow(new MemberNotFoundException("존재하지 않는 회원입니다."));

        mockMvc.perform(get("/api/members/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void register_returnsConflict_whenEmailIsDuplicated() throws Exception {
        given(memberService.signup(any(RegisterRequest.class)))
                .willThrow(new DuplicateEmailException("이미 사용 중인 이메일입니다."));

        RegisterRequest request = new RegisterRequest(
                "goorm@example.com",
                "password123",
                "password123",
                "구름"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    private Member createMember(Long id, String email, String name, Role role) {
        Member member = new Member();
        member.setId(id);
        member.setEmail(email);
        member.setPassword("password123");
        member.setName(name);
        member.setRole(role);
        return member;
    }
}
