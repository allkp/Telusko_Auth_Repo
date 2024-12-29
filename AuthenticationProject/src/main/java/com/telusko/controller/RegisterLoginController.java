package com.telusko.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.telusko.dto.UserDto;
import com.telusko.dto.UserLoginDto;
import com.telusko.model.PasswordResetToken;
import com.telusko.model.User;
import com.telusko.repo.TokenRepo;
import com.telusko.repo.UserRepo;
import com.telusko.service.UserDetailsServiceImpl;



@Controller
public class RegisterLoginController {

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	UserRepo userRepo;
	@Autowired
	TokenRepo tokenRepo;
	
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@GetMapping("/register")
	public String showRegistrationForm() {
		return "registration";
	}

	@PostMapping("/register")
	public String saveUser(@ModelAttribute UserDto userDTO) {
		User user = userDetailsService.save(userDTO);
		if (user != null)
			return "redirect:/login";
		else
			return "redirect:/register";
	}

	@GetMapping("/login")
	public String showLoginForm() {
		return "login";
	}

	@PostMapping("/login")
	public void login(@ModelAttribute UserLoginDto userLoginDTO, Model model) {
		userDetailsService.loadUserByUsername(userLoginDTO.getUsername());
	}

	@GetMapping("/userDashboard")
	public String showUserDashboardForm() {
		return "userDashboard";
	}

	@GetMapping("/forgotPassword")
	public String forgotPassword() {
		return "forgotPassword";
	}

	@PostMapping("/forgotPassword")
	public String forgotPassordProcess(@ModelAttribute UserDto userDTO) {
		String output = "";
		User user = userRepo.findByEmail(userDTO.getEmail());
		if (user != null) {
			output = userDetailsService.sendEmail(user);
		}
		if (output.equals("success")) {
			return "redirect:/forgotPassword?success";
		}
		return "redirect:/login?error";
	}

	@GetMapping("/resetPassword/{token}")
	public String resetPasswordForm(@PathVariable String token, Model model) {
		PasswordResetToken reset = tokenRepo.findByToken(token);
		if (reset != null && userDetailsService.hasExipred(reset.getExpiryDateTime())) {
			model.addAttribute("email", reset.getUser().getEmail());
			return "resetPassword";
		}
		return "redirect:/forgotPassword?error";
	}
	
	@PostMapping("/resetPassword")
	public String passwordResetProcess(@ModelAttribute User userDTO) {
		User user = userRepo.findByEmail(userDTO.getEmail());
		if(user != null) {
			user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
			userRepo.save(user);
		}
		return "redirect:/login";
	}

}
