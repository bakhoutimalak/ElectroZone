package com.formation.ecommerce.controller;

import com.formation.ecommerce.model.User;
import com.formation.ecommerce.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final UserRepository userRepository;

    public GlobalModelAdvice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute("fullName")
    public String fullName() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                String principal = String.valueOf(auth.getPrincipal());
                if (!principal.equals("anonymousUser")) {
                    return userRepository.findByUsername(auth.getName())
                            .map(u -> u.getFirstName() + " " + u.getLastName())
                            .orElse("");
                }
            }
        } catch (Exception ignored) {}
        return "";
    }
}
