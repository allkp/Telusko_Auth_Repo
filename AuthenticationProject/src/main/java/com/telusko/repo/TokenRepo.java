package com.telusko.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.telusko.model.PasswordResetToken;


@Repository
public interface TokenRepo extends JpaRepository<PasswordResetToken, Integer>{

	PasswordResetToken findByToken(String token);
	
}
