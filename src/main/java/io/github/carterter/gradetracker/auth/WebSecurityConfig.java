package io.github.carterter.gradetracker.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Configuration
@Controller
@Import(GTUserDetailsService.class)
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    private GTUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .authorizeHttpRequests((requests) -> requests
            .requestMatchers("/register").permitAll()
            .requestMatchers("/css/**", "/js/**").permitAll()
            .requestMatchers("/grades/**").hasAuthority("ROLE_TEACHER")  
            .requestMatchers("/student/**").hasAuthority("ROLE_STUDENT") 
            .anyRequest().authenticated()
        )
    
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true) 
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll());
    
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @GetMapping("register")
    public String showRegisterPage(WebRequest req, Model model) {
        return "register";
    }

    @PostMapping("register")
    public ModelAndView doRegister(@RequestParam String email, @RequestParam String username, @RequestParam String password, @RequestParam String role, HttpServletRequest request) {
        // important note: this does password encryption backend-side.
        // it would be better to use the firebase client sdk.

        boolean result = userDetailsService.createUser(
                new GTUser(
                        username,
                        passwordEncoder().encode(password),
                        email,
                        List.of()
                ),
                role
        );

        if(!result) {
            request.setAttribute("error", 1);
            return new ModelAndView("/register");
        } else {
            request.setAttribute("signupsuccess", 1);
            return new ModelAndView("forward:/login");
        }
    }
}