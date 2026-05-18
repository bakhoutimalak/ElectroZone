package com.formation.ecommerce.config;

import com.formation.ecommerce.model.Category;
import com.formation.ecommerce.model.Item;
import com.formation.ecommerce.model.User;
import com.formation.ecommerce.repository.CategoryRepository;
import com.formation.ecommerce.repository.ItemRepository;
import com.formation.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepo,
                                      ItemRepository itemRepo,
                                      CategoryRepository categoryRepo,
                                      PasswordEncoder encoder) {
        return args -> {
            // Admin
            if (!userRepo.existsByUsername("admin@electrozone.com")) {
                User admin = new User();
                admin.setFirstName("Admin");
                admin.setLastName("Système");
                admin.setEmail("admin@electrozone.com");
                admin.setUsername("admin@electrozone.com");
                admin.setPhone("0600000000");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                userRepo.save(admin);
            }
            // User test
            if (!userRepo.existsByUsername("user1@electrozone.com")) {
                User user = new User();
                user.setFirstName("Mohammed");
                user.setLastName("Alami");
                user.setEmail("user1@electrozone.com");
                user.setUsername("user1@electrozone.com");
                user.setPhone("0611111111");
                user.setAddress("123 Rue Hassan II");
                user.setCity("Fès");
                user.setRegion("Fès-Meknès");
                user.setPostalCode("30000");
                user.setPassword(encoder.encode("user123"));
                user.setRole("ROLE_USER");
                userRepo.save(user);
            }
            // Catégories
            if (categoryRepo.count() == 0) {
                categoryRepo.save(new Category("Smartphones", "Téléphones mobiles et accessoires"));
                categoryRepo.save(new Category("Ordinateurs", "Laptops, desktops et accessoires"));
                categoryRepo.save(new Category("Tablettes", "Tablettes et liseuses"));
                categoryRepo.save(new Category("Accessoires", "Claviers, souris, câbles"));
                categoryRepo.save(new Category("Écrans", "Moniteurs et écrans"));
            }
            // Articles
            if (itemRepo.count() == 0) {
                itemRepo.save(new Item("Laptop Dell XPS 15",
                        "Ordinateur portable 15 pouces, 16Go RAM, SSD 512Go",
                        12000.0, LocalDate.now().plusYears(2), 10, "Ordinateurs"));
                itemRepo.save(new Item("iPhone 15 Pro",
                        "Smartphone Apple 256Go, puce A17 Pro",
                        13999.0, LocalDate.now().plusYears(2), 8, "Smartphones"));
                itemRepo.save(new Item("Samsung Galaxy S24",
                        "Smartphone Samsung 128Go, écran AMOLED 6.2 pouces",
                        8499.0, LocalDate.now().plusYears(2), 15, "Smartphones"));
                itemRepo.save(new Item("MacBook Air M2",
                        "Ordinateur portable Apple, puce M2, 8Go RAM",
                        14500.0, LocalDate.now().plusYears(2), 5, "Ordinateurs"));
                itemRepo.save(new Item("Souris Logitech MX Master 3",
                        "Souris sans fil ergonomique, 4000 DPI",
                        650.0, LocalDate.now().plusYears(3), 30, "Accessoires"));
                itemRepo.save(new Item("Clavier Logitech MX Keys",
                        "Clavier sans fil rétroéclairé, frappe silencieuse",
                        890.0, LocalDate.now().plusYears(3), 25, "Accessoires"));
                itemRepo.save(new Item("Samsung Galaxy Tab S9",
                        "Tablette Android 11 pouces, 128Go, S-Pen inclus",
                        5800.0, LocalDate.now().plusYears(2), 12, "Tablettes"));
                itemRepo.save(new Item("iPad Air 5",
                        "Tablette Apple 10.9 pouces, puce M1, 64Go",
                        6500.0, LocalDate.now().plusYears(2), 7, "Tablettes"));
            }
        };
    }
}
