package com.formation.ecommerce.service;

import com.formation.ecommerce.model.Item;
import com.formation.ecommerce.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<Item> findAll() {
        return itemRepository.findAll().stream()
                .filter(i -> !i.isArchived()).toList();
    }

    @Transactional(readOnly = true)
    public List<Item> findAllIncludingArchived() {
        return itemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    public Item toggleArchive(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable"));
        item.setArchived(!item.isArchived());
        return itemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<Item> search(String name, String category,
                              Double minPrice, Double maxPrice, Boolean inStock) {
        boolean allEmpty = isBlank(name) && isBlank(category)
                && minPrice == null && maxPrice == null && inStock == null;
        List<Item> results;
        if (allEmpty) {
            results = itemRepository.findAll();
        } else {
            results = itemRepository.searchItems(
                    isBlank(name) ? null : name,
                    isBlank(category) ? null : category,
                    minPrice, maxPrice, inStock);
        }
        return results.stream().filter(i -> !i.isArchived()).toList();
    }

    @Transactional(readOnly = true)
    public List<String> findAllCategories() {
        return itemRepository.findAll().stream()
                .filter(i -> !i.isArchived())
                .map(Item::getCategory)
                .filter(c -> c != null && !c.isBlank())
                .distinct().sorted().toList();
    }

    public void decreaseStock(Long itemId, int quantity) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable"));
        if (item.getQuantityInStock() < quantity) {
            throw new IllegalStateException("Stock insuffisant pour : " + item.getName());
        }
        item.setQuantityInStock(item.getQuantityInStock() - quantity);
        itemRepository.save(item);
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
