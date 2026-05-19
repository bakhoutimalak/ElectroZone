package com.formation.ecommerce.controller;

import com.formation.ecommerce.model.Category;
import com.formation.ecommerce.model.Order;
import com.formation.ecommerce.model.OrderItem;
import java.util.*;
import java.util.stream.Collectors;
import com.formation.ecommerce.repository.ItemRepository;
import com.formation.ecommerce.repository.OrderRepository;
import com.formation.ecommerce.repository.UserRepository;
import com.formation.ecommerce.service.CategoryService;
import com.formation.ecommerce.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final CategoryService categoryService;

    public AdminController(ItemRepository itemRepository,
                           OrderRepository orderRepository,
                           UserRepository userRepository,
                           ItemService itemService,
                           CategoryService categoryService) {
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.itemService = itemService;
        this.categoryService = categoryService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // ── Stats de base ──────────────────────────────────────────
        model.addAttribute("totalItems", itemRepository.count());
        model.addAttribute("totalUsers", userRepository.count());

        List<Order> allOrders = orderRepository.findAll();
        long totalOrders = allOrders.stream()
            .filter(o -> o.getStatus() != Order.OrderStatus.CART).count();
        model.addAttribute("totalOrders", totalOrders);

        double chiffreAffaires = allOrders.stream()
            .filter(o -> o.getStatus() == Order.OrderStatus.CONFIRMED
                      || o.getStatus() == Order.OrderStatus.SHIPPED
                      || o.getStatus() == Order.OrderStatus.DELIVERED)
            .mapToDouble(Order::getTotalPrice).sum();
        model.addAttribute("chiffreAffaires",
            String.format("%.0f", chiffreAffaires));

        // ── Produits les plus / moins demandés ─────────────────────
        Map<String, Integer> produitQty = new LinkedHashMap<>();
        allOrders.stream()
            .filter(o -> o.getStatus() != Order.OrderStatus.CART)
            .flatMap(o -> o.getOrderItems().stream())
            .forEach(oi -> produitQty.merge(oi.getItem().getName(),
                                            oi.getQuantity(), Integer::sum));

        // Trier par quantité décroissante
        List<Map.Entry<String, Integer>> sortedProduits = produitQty.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toList());

        // Top 5 plus demandés
        List<String> topProduitsLabels = sortedProduits.stream()
            .limit(5).map(Map.Entry::getKey).collect(Collectors.toList());
        List<Integer> topProduitsData = sortedProduits.stream()
            .limit(5).map(Map.Entry::getValue).collect(Collectors.toList());

        // Top 5 moins demandés
        List<Map.Entry<String, Integer>> reversedProduits = new ArrayList<>(sortedProduits);
        Collections.reverse(reversedProduits);
        List<String> flopProduitsLabels = reversedProduits.stream()
            .limit(5).map(Map.Entry::getKey).collect(Collectors.toList());
        List<Integer> flopProduitsData = reversedProduits.stream()
            .limit(5).map(Map.Entry::getValue).collect(Collectors.toList());

        model.addAttribute("topProduitsLabels", topProduitsLabels);
        model.addAttribute("topProduitsData", topProduitsData);
        model.addAttribute("flopProduitsLabels", flopProduitsLabels);
        model.addAttribute("flopProduitsData", flopProduitsData);

        // ── Catégories les plus / moins demandées ──────────────────
        Map<String, Integer> catQty = new LinkedHashMap<>();
        allOrders.stream()
            .filter(o -> o.getStatus() != Order.OrderStatus.CART)
            .flatMap(o -> o.getOrderItems().stream())
            .forEach(oi -> {
                String cat = oi.getItem().getCategory() != null
                    ? oi.getItem().getCategory().toString() : "Autre";
                catQty.merge(cat, oi.getQuantity(), Integer::sum);
            });

        List<Map.Entry<String, Integer>> sortedCats = catQty.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toList());

        List<String> catLabels = sortedCats.stream()
            .map(Map.Entry::getKey).collect(Collectors.toList());
        List<Integer> catData = sortedCats.stream()
            .map(Map.Entry::getValue).collect(Collectors.toList());

        model.addAttribute("catLabels", catLabels);
        model.addAttribute("catData", catData);

        // ── Commandes par région (carte) ───────────────────────────
        Map<String, Long> regionOrders = allOrders.stream()
            .filter(o -> o.getStatus() != Order.OrderStatus.CART
                      && o.getUser().getRegion() != null
                      && !o.getUser().getRegion().isBlank())
            .collect(Collectors.groupingBy(
                o -> o.getUser().getRegion(), Collectors.counting()));

        // Trier par nombre de commandes décroissant
        List<Map.Entry<String, Long>> sortedRegions = regionOrders.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .collect(Collectors.toList());

        List<String> regionLabels = sortedRegions.stream()
            .map(Map.Entry::getKey).collect(Collectors.toList());
        List<Long> regionData = sortedRegions.stream()
            .map(Map.Entry::getValue).collect(Collectors.toList());

        model.addAttribute("regionLabels", regionLabels);
        model.addAttribute("regionData", regionData);

        // ── Statuts des commandes ──────────────────────────────────
        long confirmed = allOrders.stream().filter(o -> o.getStatus() == Order.OrderStatus.CONFIRMED).count();
        long shipped   = allOrders.stream().filter(o -> o.getStatus() == Order.OrderStatus.SHIPPED).count();
        long delivered = allOrders.stream().filter(o -> o.getStatus() == Order.OrderStatus.DELIVERED).count();
        long cancelled = allOrders.stream().filter(o -> o.getStatus() == Order.OrderStatus.CANCELLED).count();



        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String products(@RequestParam(required = false) String name,
                           @RequestParam(required = false) String category,
                           Model model) {
        model.addAttribute("items", itemService.findAllIncludingArchived());
        model.addAttribute("categories", categoryService.findActive());
        model.addAttribute("searchName", name);
        model.addAttribute("searchCategory", category);
        return "admin/products";
    }

    @GetMapping("/products/archive/{id}")
    public String archiveProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        itemService.toggleArchive(id);
        redirectAttributes.addFlashAttribute("success", "Statut de l'article mis à jour.");
        return "redirect:/admin/products";
    }

    // ===== CLIENTS =====
    @GetMapping("/clients")
    public String clients(Model model) {
        model.addAttribute("clients", userRepository.findAll().stream()
                .filter(u -> u.getRole().equals("ROLE_USER")).toList());
        return "admin/clients";
    }

    // ===== CATÉGORIES =====
    @GetMapping("/categories")
    public String categories(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("categories", categoryService.search(search));
        model.addAttribute("newCategory", new Category());
        model.addAttribute("search", search);
        return "admin/categories";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@Valid @ModelAttribute("newCategory") Category category,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "admin/categories";
        }
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("success", "Catégorie sauvegardée.");
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/archive/{id}")
    public String archiveCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.archive(id);
        redirectAttributes.addFlashAttribute("success", "Statut de la catégorie mis à jour.");
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Catégorie supprimée.");
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("newCategory", categoryService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable")));
        model.addAttribute("editMode", true);
        return "admin/categories";
    }
}
