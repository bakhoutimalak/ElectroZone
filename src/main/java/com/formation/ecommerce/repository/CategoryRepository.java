package com.formation.ecommerce.repository;

import com.formation.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByArchivedFalse();

    List<Category> findByArchivedTrue();

    List<Category> findByNameContainingIgnoreCase(String name);

    boolean existsByName(String name);
}
