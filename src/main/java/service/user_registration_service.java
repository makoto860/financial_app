package com.example.financial_app.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import form.user_registration_form;

@Service
public class user_registration_service {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public user_registration_service(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public boolean register(user_registration_form form) {

        if (userRepository.existsByEmail(form.getEmail())) {
            return false;
        }

        User user = new User();
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());

        // パスワードをハッシュ化して保存
        user.setPassword(
            passwordEncoder.encode(form.getPassword())
        );

        userRepository.save(user);

        return true;
    }
}
