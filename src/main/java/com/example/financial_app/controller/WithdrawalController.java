package com.example.financial_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WithdrawalController {
	@GetMapping("/withdrawal")
	public String withdrawal() {
		return "withdrawal";
	}

	@PostMapping("/withdrawal")
    public String submitWithdrawal(
            @RequestParam("amount") int amount,
            Model model) {

        if (amount <= 0) {
            model.addAttribute("message", "1円以上を入力してください。");
            return "withdrawal";
        }

        model.addAttribute("amount", amount);
        return "withdrawal_complete";
    }
}
