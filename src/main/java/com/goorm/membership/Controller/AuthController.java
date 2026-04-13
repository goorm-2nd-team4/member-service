package com.goorm.membership.Controller;

import com.goorm.membership.entity.Member;
import com.goorm.membership.entity.Role;
import com.goorm.membership.dto.LoginMemberDto;
import com.goorm.membership.dto.LoginRequestDto;
import com.goorm.membership.exception.InvalidPasswordException;
import com.goorm.membership.exception.MemberNotFoundException;
import com.goorm.membership.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final MemberService memberService;

    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("loginRequest") LoginRequestDto loginRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "login";
        }

        final Member member;
        try {
            member = memberService.login(loginRequest);
        } catch (MemberNotFoundException | InvalidPasswordException ex) {
            model.addAttribute("loginError", ex.getMessage());
            return "login";
        }

        session.setAttribute("loginMember", new LoginMemberDto(member));
        redirectAttributes.addFlashAttribute("memberName", member.getName());

        if (member.getRole() == Role.ADMIN) {
            return "redirect:/members";
        }

        return "redirect:/login-success";
    }
}
