package com.formation.ecommerce.service;

import com.formation.ecommerce.model.Category;
import com.formation.ecommerce.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Category> findActive() {
        return categoryRepository.findByArchivedFalse();
    }

    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Category> search(String name) {
        if (name == null || name.isBlank()) return categoryRepository.findAll();
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }

    public Category archive(Long id) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable"));
        cat.setArchived(!cat.isArchived());
        return categoryRepository.save(cat);
    }

    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
