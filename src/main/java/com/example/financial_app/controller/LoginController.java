
package com.example.financial_app.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.financial_app.service.EmailVerificationService;

@Controller
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationService verificationService;

    public LoginController(
            AuthenticationManager authenticationManager,
            EmailVerificationService verificationService) {
        this.authenticationManager = authenticationManager;
        this.verificationService = verificationService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String submitLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpServletRequest request,
            Model model) {

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    email, password));

            String code = verificationService.generateCode();

            // メール送信に成功してから認証情報をセッションに保存する
            verificationService.sendCode(email, code);

            HttpSession session = request.getSession(true);
            session.setAttribute("PENDING_AUTHENTICATION", authentication);
            session.setAttribute(
                    "VERIFICATION_CODE_HASH",
                    verificationService.hashCode(code));
            session.setAttribute(
                    "VERIFICATION_EXPIRES_AT",
                    System.currentTimeMillis() + 5 * 60 * 1000L);
            session.setAttribute("VERIFICATION_ATTEMPTS", 0);

            return "redirect:/verify-code";

        } catch (AuthenticationException e) {
            model.addAttribute(
                    "message",
                    "メールアドレスまたはパスワードが正しくありません。");
            return "login";

        } catch (MailException e) {
        	e.printStackTrace();

            model.addAttribute(
                    "message",
                    "認証メールを送信できませんでした。メール設定を確認してください。");
            return "login";
        }
    }
}
