package io.github.carterter.gradetracker.view;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class Home {
    @GetMapping("/")
    public String homePage(Authentication authentication, Model model) {
        if(authentication != null) {
            UserDetails details = (UserDetails) (authentication.getPrincipal());
            model.addAttribute("username", details.getUsername());
        } else {
            model.addAttribute("username", "logged-out user");
        }
        return "home";
    }
}
