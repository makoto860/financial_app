package com.example.financial_app.controller;

import java.math.BigDecimal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.financial_app.entity.User;
import com.example.financial_app.repository.UserRepository;
import com.example.financial_app.service.DepositService;

@Controller
public class DepositController {

    private final UserRepository userRepository;
    private final DepositService depositService;

    public DepositController(
            UserRepository userRepository,
            DepositService depositService) {
        this.userRepository = userRepository;
        this.depositService = depositService;
    }

    @GetMapping("/deposit")
    public String deposit() {
        return "deposit";
    }

    @PostMapping("/deposit")
    public String submitDeposit(
            @RequestParam("amount") String amountText,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        BigDecimal amount;

        try {
            if (amountText == null
                    || !amountText.matches("[0-9]+")) {
                model.addAttribute(
                    "message", "1円以上の整数を入力してください。");
                return "deposit";
            }

            amount = new BigDecimal(amountText);

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute(
                    "message", "1円以上の金額を入力してください。");
                return "deposit";
            }

            if (amount.precision() > 17) {
                model.addAttribute(
                    "message", "入金額が大きすぎます。");
                return "deposit";
            }

        } catch (NumberFormatException e) {
            model.addAttribute(
                "message", "正しい金額を入力してください。");
            return "deposit";
        }

        String email = authentication.getName();

        depositService.deposit(email, amount);

        redirectAttributes.addFlashAttribute(
            "depositedAmount", amount);

        return "redirect:/deposit/complete";
    }

    @GetMapping("/deposit/complete")
    public String depositComplete(
            Authentication authentication,
            Model model) {

        User user = userRepository.findByEmail(
                authentication.getName())
            .orElseThrow(() ->
                new IllegalStateException("ユーザーが見つかりません"));

        model.addAttribute("username", user.getUsername());
        model.addAttribute("balance", user.getBalance());

        return "deposit_complete";
    }
}
