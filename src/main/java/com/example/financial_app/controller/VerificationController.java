package com.example.financial_app.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.financial_app.service.EmailVerificationService;

@Controller
public class VerificationController {

    private final EmailVerificationService verificationService;
    private final SecurityContextRepository securityContextRepository;

    public VerificationController(
            EmailVerificationService verificationService,
            SecurityContextRepository securityContextRepository) {
        this.verificationService = verificationService;
        this.securityContextRepository = securityContextRepository;
    }

    @GetMapping("/verify-code")
    public String showVerificationPage(HttpSession session) {
        if (session.getAttribute("PENDING_AUTHENTICATION") == null) {
            return "redirect:/login";
        }
        return "verify-code";
    }

    @PostMapping("/verify-code")
    public String verifyCode(
            @RequestParam("code") String code,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session,
            Model model) {

        Authentication pendingAuthentication =
                (Authentication) session.getAttribute(
                        "PENDING_AUTHENTICATION");

        String savedHash = (String) session.getAttribute(
                "VERIFICATION_CODE_HASH");

        Object expiresObject = session.getAttribute(
                "VERIFICATION_EXPIRES_AT");

        if (pendingAuthentication == null
                || savedHash == null
                || !(expiresObject instanceof Long)) {
            clearPendingSession(session);
            return "redirect:/login";
        }

        long expiresAt = (Long) expiresObject;

        if (System.currentTimeMillis() > expiresAt) {
            clearPendingSession(session);
            model.addAttribute(
                    "message",
                    "認証番号の有効期限が切れました。もう一度ログインしてください。");
            return "login";
        }

        Integer attempts = (Integer) session.getAttribute(
                "VERIFICATION_ATTEMPTS");

        if (attempts == null) {
            attempts = 0;
        }

        if (attempts >= 5) {
            clearPendingSession(session);
            model.addAttribute(
                    "message",
                    "認証に5回失敗しました。もう一度ログインしてください。");
            return "login";
        }

        boolean validFormat = code != null && code.matches("[0-9]{6}");
        boolean matches = false;

        if (validFormat) {
            String enteredHash = verificationService.hashCode(code);

            matches = MessageDigest.isEqual(
                    savedHash.getBytes(StandardCharsets.US_ASCII),
                    enteredHash.getBytes(StandardCharsets.US_ASCII));
        }

        if (!matches) {
            attempts++;
            session.setAttribute("VERIFICATION_ATTEMPTS", attempts);

            if (attempts >= 5) {
                clearPendingSession(session);
                model.addAttribute(
                        "message",
                        "認証に5回失敗しました。もう一度ログインしてください。");
                return "login";
            }

            model.addAttribute(
                    "message",
                    "認証番号が正しくありません。もう一度入力してください。");
            return "verify-code";
        }

        // 認証成功：Spring Securityのログイン状態を確定する
        request.changeSessionId();

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();
        context.setAuthentication(pendingAuthentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context, request, response);

        clearPendingSession(session);

        return "redirect:/home";
    }

    private void clearPendingSession(HttpSession session) {
        session.removeAttribute("PENDING_AUTHENTICATION");
        session.removeAttribute("VERIFICATION_CODE_HASH");
        session.removeAttribute("VERIFICATION_EXPIRES_AT");
        session.removeAttribute("VERIFICATION_ATTEMPTS");
    }
}
