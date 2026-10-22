package com.example.financial_app.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.financial_app.entity.User;
import com.example.financial_app.repository.UserRepository;

@Service
public class WithdrawalService {
	private final UserRepository userRepository;

    public WithdrawalService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void withdraw(String email, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("1円以上を入力してください。");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                    new IllegalStateException("ユーザーが見つかりません。"));

        BigDecimal currentBalance = user.getBalance();

        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }

        if (currentBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("残高が不足しています。");
        }

        BigDecimal newBalance = currentBalance.subtract(amount);

        user.setBalance(newBalance);
        userRepository.save(user);
    }
}
