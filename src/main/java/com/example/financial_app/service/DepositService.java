package com.example.financial_app.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.financial_app.entity.User;
import com.example.financial_app.repository.UserRepository;

@Service
public class DepositService {
    private final UserRepository userRepository;

    public DepositService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void deposit(String email, BigDecimal amount) {

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "入金額は1円以上である必要があります。");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                    new IllegalStateException("ユーザーが見つかりません"));

        BigDecimal currentBalance = user.getBalance();

        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }

        BigDecimal newBalance = currentBalance.add(amount);

        if (newBalance.precision() > 19
                || newBalance.scale() > 2) {
            throw new IllegalArgumentException(
                "残高の上限を超えています。");
        }

        user.setBalance(newBalance);
        userRepository.save(user);
    }
}
