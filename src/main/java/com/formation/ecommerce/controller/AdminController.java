package com.formation.ecommerce.controller;

import com.formation.ecommerce.model.Category;
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
        model.addAttribute("totalItems", itemRepository.count());
        model.addAttribute("totalOrders", orderRepository.count());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("recentOrders", orderRepository.findAll());
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
