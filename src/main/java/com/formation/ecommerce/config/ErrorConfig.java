package com.formation.ecommerce.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorConfig implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        }
        if (statusCode == null) statusCode = 500;
        
        model.addAttribute("status", statusCode);
        
        if (statusCode == 404) {
            model.addAttribute("message", "La page que vous cherchez n'existe pas.");
        } else if (statusCode == 403) {
            model.addAttribute("message", "Accès refusé.");
        } else {
            model.addAttribute("message", "Une erreur inattendue s'est produite.");
        }
        return "error";
    }
}
