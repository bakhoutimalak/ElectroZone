package com.formation.ecommerce.controller;

import com.formation.ecommerce.model.Item;
import com.formation.ecommerce.service.CategoryService;
import com.formation.ecommerce.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.util.Base64;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final CategoryService categoryService;

    public ItemController(ItemService itemService, CategoryService categoryService) {
        this.itemService = itemService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String name,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) Double minPrice,
                       @RequestParam(required = false) Double maxPrice,
                       @RequestParam(required = false) Boolean inStock,
                       Model model) {
        model.addAttribute("items", itemService.search(name, category, minPrice, maxPrice, inStock));
        model.addAttribute("categories", itemService.findAllCategories());
        model.addAttribute("searchName", name);
        model.addAttribute("searchCategory", category);
        model.addAttribute("searchMinPrice", minPrice);
        model.addAttribute("searchMaxPrice", maxPrice);
        model.addAttribute("searchInStock", inStock);
        return "items/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Item item = itemService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable"));
        model.addAttribute("item", item);
        model.addAttribute("related", itemService.findByCategory(item.getCategory(), item.getId()));
        return "items/detail";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newForm(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("isEdit", false);
        model.addAttribute("categories", categoryService.findActive());
        return "items/form";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("item", itemService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable")));
        model.addAttribute("isEdit", true);
        model.addAttribute("categories", categoryService.findActive());
        return "items/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String save(@Valid @ModelAttribute("item") Item item,
                       BindingResult result,
                       @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                       Model model,
                       RedirectAttributes redirectAttributes) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", item.getId() != null);
            model.addAttribute("categories", categoryService.findActive());
            return "items/form";
        }
        // Traitement de l'image
        if (imageFile != null && !imageFile.isEmpty()) {
            String base64 = Base64.getEncoder().encodeToString(imageFile.getBytes());
            String mimeType = imageFile.getContentType();
            item.setImageUrl("data:" + mimeType + ";base64," + base64);
        }
        itemService.save(item);
        redirectAttributes.addFlashAttribute("success", "Article sauvegardé avec succès !");
        return "redirect:/admin/products";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        itemService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Article supprimé.");
        return "redirect:/admin/products";
    }
}
