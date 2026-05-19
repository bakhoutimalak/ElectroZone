package com.formation.ecommerce.controller;

import com.formation.ecommerce.model.Order;
import com.formation.ecommerce.service.DeliveryService;
import com.formation.ecommerce.repository.UserRepository;
import com.formation.ecommerce.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final DeliveryService deliveryService;

    public CartController(OrderService orderService, UserRepository userRepository, DeliveryService deliveryService) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.deliveryService = deliveryService;
    }

    @GetMapping
    public String viewCart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/auth/login";
        try {
            Order cart = orderService.getOrCreateCart(userDetails.getUsername());
            model.addAttribute("cart", cart);
            // Passer le user séparément pour éviter lazy loading
            com.formation.ecommerce.model.User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
            model.addAttribute("currentUserObj", user);
            String region = user != null ? user.getRegion() : null;
            double total = cart.getTotalPrice();
            int frais = deliveryService.getFraisLivraison(region, total);
            int fraisBase = deliveryService.getFraisBase(region);
            int seuil = deliveryService.getSeuilGratuit(region);
            String delai = deliveryService.getDelai(region);
            model.addAttribute("fraisLivraison", frais);
            model.addAttribute("fraisBase", fraisBase);
            model.addAttribute("seuilGratuit", seuil);
            model.addAttribute("delaiLivraison", delai);
            model.addAttribute("totalAvecLivraison", total + frais);
            return "cart/cart";
        } catch (Exception e) {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/add/{itemId}")
    public String addToCart(@PathVariable Long itemId,
                            @RequestParam(defaultValue = "1") int quantity,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/auth/login";
        try {
            orderService.addToCart(userDetails.getUsername(), itemId, quantity);
            redirectAttributes.addFlashAttribute("success", "Article ajouté au panier !");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }
        return "redirect:/items";
    }

    @PostMapping("/update/{orderItemId}")
    public String updateItem(@PathVariable Long orderItemId,
                             @RequestParam int quantity,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/auth/login";
        try {
            orderService.updateCartItem(userDetails.getUsername(), orderItemId, quantity);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @GetMapping("/remove/{orderItemId}")
    public String removeItem(@PathVariable Long orderItemId,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/auth/login";
        try {
            orderService.removeFromCart(userDetails.getUsername(), orderItemId);
            redirectAttributes.addFlashAttribute("success", "Article retiré du panier.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/confirm-cod")
    public String confirmCod(@AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/auth/login";
        try {
            Order order = orderService.confirmCart(userDetails.getUsername());
            order.setPaymentMethod(Order.PaymentMethod.COD);
            order.setPaymentStatus(Order.PaymentStatus.PENDING);
            orderService.save(order);
            redirectAttributes.addFlashAttribute("success", "Commande confirmée ! Paiement à la livraison.");
            return "redirect:/orders";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @PostMapping("/confirm-cmi")
    public String confirmCmi(@AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/auth/login";
        try {
            Order order = orderService.confirmCart(userDetails.getUsername());
            return "redirect:/payment/cmi/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @PostMapping("/confirm")
    public String confirmOrder(@AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/auth/login";
        try {
            Order order = orderService.confirmCart(userDetails.getUsername());
            redirectAttributes.addFlashAttribute("success",
                    "Commande #" + order.getId() + " confirmée !");
            return "redirect:/orders";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }
}
