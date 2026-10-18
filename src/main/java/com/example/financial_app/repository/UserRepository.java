package com.example.financial_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.financial_app.entity.user;

public interface user_repository extends JpaRepository<user, Long> {
	boolean existsByEmail(String email);
}
