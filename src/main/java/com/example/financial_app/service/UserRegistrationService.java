package com.example.financial_app.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.financial_app.entity.User;
import com.example.financial_app.form.UserRegistrationForm;
import com.example.financial_app.repository.UserRepository;

@Service
public class UserRegistrationService {

    private final UserRepository UserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.UserRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional
    public boolean register(UserRegistrationForm form) {

        if (UserRepository.existsByEmail(form.getEmail())) {
            return false;
        }

        User user = new User();
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());

        // パスワードをハッシュ化して保存
        user.setPassword(
            passwordEncoder.encode(form.getPassword())
        );

        UserRepository.save(user);

        return true;
    }
}
