package com.telusko.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.telusko.service.UserDetailsServiceImpl;


@Configuration
@EnableWebSecurity
public class SpringSecurity {
	

		    @Autowired
		    private UserDetailsServiceImpl userDetailsService;
		    
		    @Autowired
		    private JwtFilter jwtFilter;
		  
		    @Bean
		    public BCryptPasswordEncoder passwordEncoder() {
		        return new BCryptPasswordEncoder();
		    }
			
			@Bean
		    public DaoAuthenticationProvider authenticationProvider() {
		        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		        auth.setUserDetailsService(userDetailsService);
		        auth.setPasswordEncoder(passwordEncoder());
		        return auth;
		    }
			
			public AuthenticationManager authenticationManager(AuthenticationConfiguration
					authenticationConfiguration) throws Exception {
		        return authenticationConfiguration.getAuthenticationManager();
		    }
				    
			@Bean
			public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
			    return http
			        .authorizeHttpRequests(auth -> auth
			            .requestMatchers("/register", "/forgotPassword", "/resetPassword/**").permitAll()
			            .anyRequest().authenticated()
			        )
			        .formLogin(form -> form
			            .loginPage("/login")
			            .defaultSuccessUrl("/userDashboard")
			            .permitAll()
			        )
			        .logout(logout -> logout.permitAll())
			        .csrf(csrf -> csrf.disable())  // CSRF protection disabled
			        .headers(headers -> headers.cacheControl(cache -> cache.disable())) // Enabling cache control
					.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
			        .build();
			}


}
