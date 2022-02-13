package com.project.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUp(@RequestBody @Validated SignUpForm signUpForm) {
        if (signUpForm.getEmail().equals("dolla_@naver.com")) {
            throw new IllegalStateException("중복 이메일");
        }
        System.out.println("signUpForm.getEmail() = " + signUpForm.getEmail());
        System.out.println("signUpForm.getNickname() = " + signUpForm.getNickname());
        return "/";
    }
}
