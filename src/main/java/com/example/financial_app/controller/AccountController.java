package com.example.financial_app.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.financial_app.entity.User;
import com.example.financial_app.repository.UserRepository;

@Controller
public class AccountController {

    private final UserRepository userRepository;

    public AccountController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/account")
    public String account(
            Authentication authentication,
            Model model) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                    new IllegalStateException("ユーザーが見つかりません"));

        model.addAttribute("username", user.getUsername());
        model.addAttribute("balance", user.getBalance());

        return "account";
    }
}
