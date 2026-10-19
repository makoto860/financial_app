package com.example.financial_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.financial_app.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // メールアドレスが登録済みか確認する
    boolean existsByEmail(String email);
    // メールアドレスからユーザーを取得する
    Optional<User> findByEmail(String email);
}
