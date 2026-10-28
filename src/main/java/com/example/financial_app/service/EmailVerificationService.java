//認証サービス
package com.example.financial_app.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final SecureRandom random = new SecureRandom();

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailVerificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // 6桁の認証番号を生成する
    public String generateCode() {
        return String.format(Locale.ROOT, "%06d",
                random.nextInt(1_000_000));
    }

    // 認証番号をハッシュ化する
    public String hashCode(String code) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    code.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "認証番号を処理できません。", e);
        }
    }

    // Gmailで認証番号を送信する
    public void sendCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(to);
        message.setSubject("financial_app メール認証番号");

        message.setText(
                "ログイン認証番号は " + code + " です。\n"
                + "この番号の有効期限は5分です。\n"
                + "心当たりがない場合は、このメールを無視してください。");

        mailSender.send(message);
    }
    
    //ユーザー毎にメールに暗証番号を届くようにする
    public void sendVerificationCode(
            String email, String code) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        // 利用者ごとの送信先
        message.setTo(email);

        message.setSubject("ログイン認証コード");
        message.setText(
                "あなたの認証コードは " + code + " です。");

        mailSender.send(message);
    }
}
