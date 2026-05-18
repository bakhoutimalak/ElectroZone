package com.formation.ecommerce.config;

import com.formation.ecommerce.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class ThymeleafConfig {

    private final UserRepository userRepository;

    public ThymeleafConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    @RequestScope
    public CurrentUserBean currentUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()
                    && !"anonymousUser".equals(auth.getPrincipal())) {
                return userRepository.findByUsername(auth.getName())
                        .map(u -> new CurrentUserBean(
                                u.getFirstName() + " " + u.getLastName(),
                                u.getAvatarUrl(),
                                u.getEmail()))
                        .orElse(new CurrentUserBean("", null, ""));
            }
        } catch (Exception e) {
            // visiteur non connecté
        }
        return new CurrentUserBean("", null, "");
    }

    public static class CurrentUserBean {
        private final String fullName;
        private final String avatarUrl;
        private final String email;

        public CurrentUserBean(String fullName, String avatarUrl, String email) {
            this.fullName = fullName != null ? fullName : "";
            this.avatarUrl = avatarUrl;
            this.email = email != null ? email : "";
        }

        public String getFullName() { return fullName; }
        public String getAvatarUrl() { return avatarUrl; }
        public String getEmail() { return email; }
        public boolean isBlank() { return fullName.isBlank(); }
    }
}
