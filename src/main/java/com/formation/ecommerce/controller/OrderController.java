package com.formation.ecommerce.controller;

import com.formation.ecommerce.model.Order;
import com.formation.ecommerce.model.Order.OrderStatus;
import com.formation.ecommerce.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String myOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("orders", orderService.findAllByUsername(userDetails.getUsername()));
        return "orders/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails userDetails,
                         Model model) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable"));
        model.addAttribute("order", order);
        return "orders/detail";
    }

    // Client peut annuler sa commande si CONFIRMED
    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable"));

        if (order.getStatus() == OrderStatus.CONFIRMED) {
            orderService.updateStatus(id, OrderStatus.CANCELLED);
            redirectAttributes.addFlashAttribute("success", "Commande annulée.");
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Impossible d'annuler une commande en statut : " + order.getStatus());
        }
        return "redirect:/orders";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String allOrders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        model.addAttribute("statuses", OrderStatus.values());
        return "orders/admin-list";
    }

    @PostMapping("/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam OrderStatus status,
                               RedirectAttributes redirectAttributes) {
        orderService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Statut mis à jour.");
        return "redirect:/orders/admin";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        orderService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Commande supprimée.");
        return "redirect:/orders/admin";
    }
}
