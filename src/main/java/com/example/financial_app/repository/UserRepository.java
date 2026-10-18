package com.example.financial_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.financial_app.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);
}
