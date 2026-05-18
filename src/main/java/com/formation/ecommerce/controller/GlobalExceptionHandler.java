package com.formation.ecommerce.controller;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleNotFound(IllegalArgumentException ex, Model model) {
        model.addAttribute("errorTitle", "Ressource introuvable");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/generic";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleBusinessError(IllegalStateException ex, Model model) {
        model.addAttribute("errorTitle", "Erreur");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/generic";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(Model model) {
        model.addAttribute("errorTitle", "Accès refusé");
        model.addAttribute("errorMessage", "Vous n'avez pas les droits pour cette page.");
        return "error/generic";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception ex, Model model) {
        model.addAttribute("errorTitle", "Erreur interne");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/generic";
    }
}
