package com.goorm.membership.Controller;

import com.goorm.membership.dto.LoginRequestDto;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequestDto());
        }
        return "login";
    }

    @GetMapping("/login-success")
    public String loginSuccess() {
        return "login-success";
    }
}
