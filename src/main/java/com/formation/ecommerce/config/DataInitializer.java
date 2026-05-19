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
                Item item_Laptop_Del = new Item("Laptop Dell XPS 15",
                        "Le Dell XPS 15 est un ordinateur portable haut de gamme concu pour les professionnels et les createurs de contenu.\n\n"
                        + "Caracteristiques techniques :\n"
                        + "- Processeur : Intel Core i7 12e generation\n"
                        + "- Memoire RAM : 16 Go DDR5\n"
                        + "- Stockage : SSD NVMe 512 Go\n"
                        + "- Ecran : 15,6 pouces OLED 3.5K 60Hz\n"
                        + "- Carte graphique : NVIDIA RTX 3050 Ti 4Go\n"
                        + "- Batterie : 86 Wh, autonomie jusqu a 13 heures\n"
                        + "- Systeme : Windows 11 Pro\n\n"
                        + "Points forts :\n"
                        + "- Design ultra-fin en aluminium premium\n"
                        + "- Clavier retroeclaire confortable\n"
                        + "- Connectique complete : Thunderbolt 4, USB-A, HDMI\n\n"
                        + "Ideal pour le developpement, la creation graphique et les presentations professionnelles.",
                        12000.0, LocalDate.now().plusYears(2), 10, "Ordinateurs");
                item_Laptop_Del.setImageUrl("https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=400");
                itemRepo.save(item_Laptop_Del);                Item item_iPhone_15_ = new Item("iPhone 15 Pro",
                        "L iPhone 15 Pro represente le summum de la technologie mobile Apple avec la puce A17 Pro gravee en 3 nm.\n\n"
                        + "Caracteristiques techniques :\n"
                        + "- Processeur : Apple A17 Pro (3 nm)\n"
                        + "- Stockage : 256 Go NVMe\n"
                        + "- Ecran : Super Retina XDR 6,1 pouces ProMotion 120Hz\n"
                        + "- Triple camera : 48MP + 12MP ultra grand-angle + 12MP telephoto 3x\n"
                        + "- Camera frontale : TrueDepth 12MP\n"
                        + "- Batterie : charge rapide 27W\n"
                        + "- Connecteur : USB-C avec USB 3\n"
                        + "- Chassis : Titane de qualite aerospatiale\n\n"
                        + "Points forts :\n"
                        + "- Bouton Action personnalisable\n"
                        + "- Mode Cinematique 4K 60fps\n"
                        + "- Wi-Fi 6E et Bluetooth 5.3\n\n"
                        + "Le choix ideal pour les utilisateurs exigeants qui veulent le meilleur de la technologie mobile.",
                        13999.0, LocalDate.now().plusYears(2), 8, "Smartphones");
                item_iPhone_15_.setImageUrl("https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=400");
                itemRepo.save(item_iPhone_15_);                Item item_Samsung_Ga = new Item("Samsung Galaxy S24",
                        "Le Samsung Galaxy S24 offre une experience Android premium avec Galaxy AI integre.\n\n"
                        + "Caracteristiques techniques :\n"
                        + "- Processeur : Snapdragon 8 Gen 3 (4 nm)\n"
                        + "- Memoire RAM : 8 Go\n"
                        + "- Stockage : 128 Go UFS 4.0\n"
                        + "- Ecran : Dynamic AMOLED 2X 6,2 pouces 120Hz\n"
                        + "- Triple camera : 50MP + 12MP + 10MP telephoto 3x\n"
                        + "- Batterie : 4000 mAh, charge 25W\n"
                        + "- Resistance : IP68\n\n"
                        + "Points forts :\n"
                        + "- Galaxy AI : traduction temps reel, resume de texte\n"
                        + "- Ecran ultra lumineux 2600 nits\n"
                        + "- Mise a jour Android garantie 7 ans\n\n"
                        + "Parfait pour les utilisateurs Android qui recherchent performance et innovation.",
                        8499.0, LocalDate.now().plusYears(2), 15, "Smartphones");
                item_Samsung_Ga.setImageUrl("https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=400");
                itemRepo.save(item_Samsung_Ga);                Item item_MacBook_Ai = new Item("MacBook Air M2",
                        "Le MacBook Air M2 redefinit le concept d ordinateur portable avec la puce Apple M2 et son design sans ventilateur.\n\n"
                        + "Caracteristiques techniques :\n"
                        + "- Processeur : Apple M2 (8 coeurs CPU, 8 coeurs GPU)\n"
                        + "- Memoire unifiee : 8 Go\n"
                        + "- Stockage : SSD 256 Go\n"
                        + "- Ecran : Liquid Retina 13,6 pouces 2560x1664\n"
                        + "- Webcam : 1080p FaceTime HD\n"
                        + "- Batterie : autonomie jusqu a 18 heures\n"
                        + "- Poids : 1,24 kg seulement\n\n"
                        + "Points forts :\n"
                        + "- Design ultra-fin sans ventilateur totalement silencieux\n"
                        + "- Charge MagSafe et deux ports Thunderbolt 4\n"
                        + "- macOS Sonoma avec toutes les applications Apple\n\n"
                        + "L ordinateur parfait pour les etudiants, creatifs et professionnels en deplacement.",
                        14500.0, LocalDate.now().plusYears(2), 5, "Ordinateurs");
                item_MacBook_Ai.setImageUrl("https://images.unsplash.com/photo-1611186871525-2f7f70e5b65c?w=400");
                itemRepo.save(item_MacBook_Ai);                Item item_Souris_Log = new Item("Souris Logitech MX Master 3",
                        "La Logitech MX Master 3 est la souris sans fil de reference pour les professionnels et power users.\n\n"
                        + "Caracteristiques techniques :\n"
                        + "- Capteur : Darkfield 4000 DPI haute precision\n"
                        + "- Connexion : Bluetooth + recepteur USB Unifying\n"
                        + "- Autonomie : 70 jours sur charge complete\n"
                        + "- Charge : USB-C, 1 minute = 3 heures d utilisation\n"
                        + "- Molette : MagSpeed electromagnetique\n"
                        + "- Boutons : 7 boutons personnalisables\n\n"
                        + "Points forts :\n"
                        + "- Molette MagSpeed ultra-rapide et precise\n"
                        + "- Ergonomie parfaite pour les longues sessions\n"
                        + "- Connexion simultanee jusqu a 3 appareils\n\n"
                        + "La souris ideale pour augmenter votre productivite au quotidien.",
                        650.0, LocalDate.now().plusYears(3), 30, "Accessoires");
                item_Souris_Log.setImageUrl("https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400");
                itemRepo.save(item_Souris_Log);                Item item_Clavier_Lo = new Item("Clavier Logitech MX Keys",
                        "Le Logitech MX Keys est un clavier sans fil premium pour une frappe confortable et precise.\n\n"
                        + "Caracteristiques techniques :\n"
                        + "- Connexion : Bluetooth + recepteur Logi Bolt\n"
                        + "- Autonomie : 10 jours avec retroeclairage, 5 mois sans\n"
                        + "- Charge : USB-C\n"
                        + "- Retroeclairage adaptatif selon la luminosite ambiante\n"
                        + "- Touches concaves spheriques pour positionnement precis\n\n"
                        + "Points forts :\n"
                        + "- Frappe silencieuse et confortable\n"
                        + "- Connexion simultanee a 3 appareils\n"
                        + "- Design elegant en aluminium brosse\n\n"
                        + "Le clavier parfait pour les professionnels qui tapent beaucoup.",
                        890.0, LocalDate.now().plusYears(3), 25, "Accessoires");
                item_Clavier_Lo.setImageUrl("https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400");
                itemRepo.save(item_Clavier_Lo);                Item item_Samsung_Tab = new Item("Samsung Galaxy Tab S9",
                        "La Samsung Galaxy Tab S9 est la tablette Android premium avec le S Pen inclus.\n\n"
                        + "Caracteristiques techniques :\n"
                        + "- Processeur : Snapdragon 8 Gen 2 (4 nm)\n"
                        + "- Memoire RAM : 8 Go\n"
                        + "- Stockage : 128 Go extensible microSD\n"
                        + "- Ecran : Dynamic AMOLED 2X 11 pouces 120Hz\n"
                        + "- Camera : 13MP arriere + 12MP avant\n"
                        + "- Batterie : 8400 mAh charge 45W\n"
                        + "- S Pen inclus dans la boite\n"
                        + "- Resistance : IP68\n\n"
                        + "Points forts :\n"
                        + "- S Pen avec latence ultra-faible 2,8ms\n"
                        + "- Samsung DeX pour experience desktop\n"
                        + "- Mise a jour Android garantie 4 ans\n\n"
                        + "La tablette ideale pour les artistes numeriques et etudiants.",
                        5800.0, LocalDate.now().plusYears(2), 12, "Tablettes");
                item_Samsung_Tab.setImageUrl("https://images.unsplash.com/photo-1561154464-82e9adf32764?w=400");
                itemRepo.save(item_Samsung_Ga);                Item item_iPad_Air_5 = new Item("iPad Air 5",
                        "L iPad Air 5 combine la puissance de la puce M1 avec la polyvalence d une tablette legere et elegante.\n\n"
                        + "Caracteristiques techniques :\n"
                        + "- Processeur : Apple M1 (8 coeurs)\n"
                        + "- Stockage : 64 Go\n"
                        + "- Ecran : Liquid Retina 10,9 pouces 2360x1640\n"
                        + "- Camera : 12MP arriere Smart HDR 4\n"
                        + "- Camera avant : 12MP ultra grand-angle\n"
                        + "- Connecteur : USB-C USB 3.1 Gen 2\n"
                        + "- Compatible : Apple Pencil 2e gen + Magic Keyboard\n\n"
                        + "Points forts :\n"
                        + "- Performances M1 identiques au MacBook Air\n"
                        + "- Touch ID dans le bouton lateral\n"
                        + "- iPadOS avec Stage Manager pour multitache\n\n"
                        + "Parfait pour les etudiants et creatifs qui veulent la puissance Apple.",
                        6500.0, LocalDate.now().plusYears(2), 7, "Tablettes");
                item_iPad_Air_5.setImageUrl("https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400");
                itemRepo.save(item_iPad_Air_5);            }
        };
    }
}
