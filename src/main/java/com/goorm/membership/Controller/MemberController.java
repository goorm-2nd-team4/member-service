package com.goorm.membership.Controller;

import com.goorm.membership.Model.Member;
import com.goorm.membership.Model.Role;
import com.goorm.membership.dto.LoginRequestDto;
import com.goorm.membership.dto.SignupRequestDto;
import com.goorm.membership.exception.DuplicateEmailException;
import com.goorm.membership.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/new-form")
    public String newForm(Model model) {
        if (!model.containsAttribute("signupRequest")) {
            model.addAttribute("signupRequest", new SignupRequestDto());
        }
        return "new-form";
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("signupRequest") SignupRequestDto signupRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "new-form";
        }

        try {
            memberService.signup(signupRequest);
        } catch (DuplicateEmailException ex) {
            bindingResult.rejectValue("email", "duplicate", ex.getMessage());
            return "new-form";
        }

        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("loginRequest") LoginRequestDto loginRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "login";
        }

        Optional<Member> loginMember = memberService.login(loginRequest);
        if (loginMember.isEmpty()) {
            model.addAttribute("loginError", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return "login";
        }

        Member member = loginMember.get();
        redirectAttributes.addFlashAttribute("memberName", member.getName());

        if (member.getRole() == Role.ADMIN) {
            return "redirect:/members";
        }

        return "redirect:/login-success";
    }

    @GetMapping
    public String members(Model model) {
        List<Member> members = memberService.findAll();
        model.addAttribute("members", members);
        return "members";
    }
}
