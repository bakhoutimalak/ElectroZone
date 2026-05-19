package com.formation.ecommerce.controller;

import com.formation.ecommerce.model.Order;
import com.formation.ecommerce.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final OrderService orderService;

    public PaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Page choix du mode de paiement
    @GetMapping("/choose/{orderId}")
    public String choosePage(@PathVariable Long orderId, Model model) {
        Order order = orderService.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable"));
        model.addAttribute("order", order);
        return "payment/choose";
    }

    // Paiement à la livraison
    @PostMapping("/cod/{orderId}")
    public String codPayment(@PathVariable Long orderId,
                             RedirectAttributes redirectAttributes) {
        Order order = orderService.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable"));
        order.setPaymentMethod(Order.PaymentMethod.COD);
        order.setPaymentStatus(Order.PaymentStatus.PENDING);
        orderService.save(order);
        redirectAttributes.addFlashAttribute("success",
                "Commande confirmee ! Paiement a la livraison.");
        return "redirect:/orders";
    }

    // Page formulaire CMI
    @GetMapping("/cmi/{orderId}")
    public String cmiPage(@PathVariable Long orderId, Model model) {
        Order order = orderService.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable"));
        model.addAttribute("order", order);
        return "payment/cmi";
    }

    // Traitement paiement CMI
    @PostMapping("/cmi/process/{orderId}")
    public String processCmi(@PathVariable Long orderId,
                             @RequestParam String cardNumber,
                             @RequestParam String cardHolder,
                             @RequestParam String expiry,
                             @RequestParam String cvv,
                             RedirectAttributes redirectAttributes) {
        Order order = orderService.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable"));

        // Validation basique
        String cleanCard = cardNumber.replaceAll("\\s", "");
        if (!isValidLuhn(cleanCard) || cleanCard.length() < 16) {
            redirectAttributes.addFlashAttribute("error",
                    "Numero de carte invalide. Veuillez verifier vos informations.");
            return "redirect:/payment/cmi/" + orderId;
        }

        // Simulation paiement accepte
        order.setPaymentMethod(Order.PaymentMethod.CMI);
        order.setPaymentStatus(Order.PaymentStatus.PAID);
        order.setStatus(Order.OrderStatus.CONFIRMED);
        orderService.save(order);

        return "redirect:/payment/success/" + orderId;
    }

    // Page succès
    @GetMapping("/success/{orderId}")
    public String successPage(@PathVariable Long orderId, Model model) {
        Order order = orderService.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable"));
        model.addAttribute("order", order);
        return "payment/success";
    }

    // Algorithme de Luhn pour valider le numéro de carte
    private boolean isValidLuhn(String number) {
        try {
            int sum = 0;
            boolean alternate = false;
            for (int i = number.length() - 1; i >= 0; i--) {
                int n = Integer.parseInt(String.valueOf(number.charAt(i)));
                if (alternate) {
                    n *= 2;
                    if (n > 9) n -= 9;
                }
                sum += n;
                alternate = !alternate;
            }
            return sum % 10 == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
