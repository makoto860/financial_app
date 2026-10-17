package com.example.financial_app.controller;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.financial_app.service.UserRegistrationService;

import form.user_registration_form;

@Controller
public class user_registration_controller {

    private final UserRegistrationService registrationService;

    public user_registration_controller(
            UserRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute(
            "userRegistrationForm",
            new user_registration_form()
        );

        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("userRegistrationForm")
            user_registration_form form,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        boolean registered = registrationService.register(form);

        if (!registered) {
            model.addAttribute(
                "emailError",
                "このメールアドレスはすでに登録されています"
            );
            return "register";
        }

        return "redirect:/register/complete";
    }

    @GetMapping("/register/complete")
    public String showCompletePage() {
        return "register-complete";
    }
}
