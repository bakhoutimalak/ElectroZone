package com.formation.ecommerce.controller;

import com.formation.ecommerce.model.User;
import com.formation.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           BindingResult result,
                           @RequestParam(required = false) String confirmPassword,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (result.hasErrors()) return "auth/register";

        if (confirmPassword != null && !confirmPassword.equals(user.getPassword())) {
            model.addAttribute("passwordError", "Les mots de passe ne correspondent pas");
            return "auth/register";
        }

        try {
            userService.register(user);
            redirectAttributes.addFlashAttribute("success",
                    "Compte créé avec succès ! Connectez-vous.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("emailError", e.getMessage());
            return "auth/register";
        }
    }
}
