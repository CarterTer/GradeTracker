package io.github.carterter.gradetracker.auth;

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
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

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
                        .requestMatchers("/", "/home").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/grades/**").hasAuthority("ROLE_TEACHER")  // 仅教师可访问录入成绩
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)  // 登录后跳转到 /home
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
    public View doRegister(@RequestParam String email, @RequestParam String username, @RequestParam String password, @RequestParam String role) {
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

        RedirectView target = new RedirectView();

        // fixme: this should really be better (show an error message, ...).
        if(!result) {
            target.setUrl("/register");
        } else {
            target.setUrl("/login");
        }

        return target;
    }
}