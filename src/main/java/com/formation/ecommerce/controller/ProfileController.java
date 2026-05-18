package com.formation.ecommerce.controller;

import com.formation.ecommerce.model.User;
import com.formation.ecommerce.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.util.Base64;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/auth/login";
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElse(null);
        if (user == null) return "redirect:/auth/login";
        model.addAttribute("user", user);
        return "profile/profile";
    }

    @PostMapping("/update")
    public String update(@AuthenticationPrincipal UserDetails userDetails,
                         @RequestParam String firstName,
                         @RequestParam String lastName,
                         @RequestParam String email,
                         @RequestParam String phone,
                         @RequestParam(required = false) String address,
                         @RequestParam(required = false) String city,
                         @RequestParam(required = false) String region,
                         @RequestParam(required = false) String postalCode,
                         @RequestParam(required = false) String newPassword,
                         @RequestParam(required = false) String confirmPassword,
                         @RequestParam(required = false) MultipartFile avatar,
                         HttpServletRequest request,
                         RedirectAttributes redirectAttributes) throws IOException {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setAddress(address);
        user.setCity(city);
        user.setRegion(region);
        user.setPostalCode(postalCode);

        // Mise à jour email + username
        boolean emailChanged = false;
        if (email != null && !email.isBlank() && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("error", "Cet email est déjà utilisé.");
                return "redirect:/profile";
            }
            user.setEmail(email);
            user.setUsername(email);
            emailChanged = true;
        }

        // Photo de profil
        if (avatar != null && !avatar.isEmpty()) {
            String base64 = Base64.getEncoder().encodeToString(avatar.getBytes());
            String mime = avatar.getContentType();
            user.setAvatarUrl("data:" + mime + ";base64," + base64);
        }

        // Mot de passe
        if (newPassword != null && !newPassword.isBlank()) {
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error",
                        "Les mots de passe ne correspondent pas.");
                return "redirect:/profile";
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(user);

        // Si email changé, invalider session et forcer reconnexion
        if (emailChanged) {
            request.getSession().invalidate();
            redirectAttributes.addFlashAttribute("success",
                "Profil mis à jour ! Reconnectez-vous avec votre nouvel email.");
            return "redirect:/auth/login";
        }

        redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès !");
        return "redirect:/profile";
    }
}
