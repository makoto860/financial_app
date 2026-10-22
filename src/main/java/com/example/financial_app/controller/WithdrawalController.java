
package com.example.financial_app.controller;

import java.math.BigDecimal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.financial_app.service.WithdrawalService;

@Controller
public class WithdrawalController {
    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @GetMapping("/withdrawal")
    public String withdrawal() {
        return "withdrawal";
    }

    @PostMapping("/withdrawal")
    public String submitWithdrawal(
            @RequestParam("amount") String amountText,
            Authentication authentication,
            Model model) {

        if (amountText == null || !amountText.matches("[0-9]+")) {
            model.addAttribute("message", "1円以上の整数を入力してください。");
            return "withdrawal";
        }

        BigDecimal amount;

        try {
            amount = new BigDecimal(amountText);
        } catch (NumberFormatException e) {
            model.addAttribute("message", "正しい金額を入力してください。");
            return "withdrawal";
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("message", "1円以上を入力してください。");
            return "withdrawal";
        }

        try {
            withdrawalService.withdraw(authentication.getName(), amount);
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", e.getMessage());
            return "withdrawal";
        }

        model.addAttribute("amount", amount);
        return "withdrawal_complete";
    }
}

