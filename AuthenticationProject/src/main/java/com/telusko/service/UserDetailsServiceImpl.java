package com.telusko.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.telusko.dto.UserDto;
import com.telusko.model.PasswordResetToken;
import com.telusko.model.User;
import com.telusko.repo.TokenRepo;
import com.telusko.repo.UserRepo;


@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private UserRepo userRepo;
	
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private TokenRepo tokenRepo;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepo.findByEmail(email);
		
		if(user == null) {
			throw new UsernameNotFoundException("Invalid userName or password...!!!");
		}
		
		
		
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new HashSet<GrantedAuthority>());
	}
	
	public User save(UserDto userDto) {
		User user = new User();
		user.setEmail(userDto.getEmail());
		user.setName(userDto.getName());
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		return userRepo.save(user);
	}
	
	public String sendEmail(User user) {
		try {
			String resetLink = generateResetToken(user);
			
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setFrom("lokeshadireddim201@gmail.com");
			msg.setTo(user.getEmail());
			
			msg.setSubject("Hi My dear friend... I am Lokesh.....!!!");
			msg.setText("Hello \n\n" + "Please click on this link to Reset your Password :" + resetLink + ". \n\n" + "Regards from \n" + "ALLKP...!!!");
			
			javaMailSender.send(msg);
			
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error Occured...!!!";
		}
	}

	private String generateResetToken(User user) {
		UUID uuid = UUID.randomUUID();
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime expiryDateTime = currentDateTime.plusMinutes(30);
		PasswordResetToken resetToken = new PasswordResetToken();
		resetToken.setUser(user);
		resetToken.setToken(uuid.toString());
		resetToken.setExpiryDateTime(expiryDateTime);
		resetToken.setUser(user);
		
		PasswordResetToken token = tokenRepo.save(resetToken);
		if(token != null) {
			String endPointUrl = "http://loalhost:8081/resetPassword";
			return endPointUrl + "/" + resetToken.getToken();
		}
		
		return "generateResetToken() method completed...!!!";
	}
	
	public boolean hasExipred(LocalDateTime expiryDateTime) {
		LocalDateTime currentDateTime = LocalDateTime.now();
		return expiryDateTime.isAfter(currentDateTime);
	}

}





