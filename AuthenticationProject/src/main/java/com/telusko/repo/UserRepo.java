package com.telusko.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.telusko.model.User;


@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

	User findByEmail(String email);

}
