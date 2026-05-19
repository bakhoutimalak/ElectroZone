package com.formation.ecommerce.service;

import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class DeliveryService {

    // Frais par région
    private static final Map<String, Integer> FRAIS = Map.ofEntries(
        Map.entry("Grand Casablanca-Settat", 30),
        Map.entry("Casablanca", 30),
        Map.entry("Rabat-Salé-Kénitra", 30),
        Map.entry("Rabat", 30),
        Map.entry("Fès-Meknès", 40),
        Map.entry("Fès", 40),
        Map.entry("Meknès", 40),
        Map.entry("Marrakech-Safi", 40),
        Map.entry("Marrakech", 40),
        Map.entry("Tanger-Tétouan-Al Hoceïma", 45),
        Map.entry("Tanger", 45),
        Map.entry("Souss-Massa", 45),
        Map.entry("Agadir", 45),
        Map.entry("Oriental", 45),
        Map.entry("Oujda", 45),
        Map.entry("Béni Mellal-Khénifra", 45),
        Map.entry("Drâa-Tafilalet", 55),
        Map.entry("Guelmim-Oued Noun", 60),
        Map.entry("Laâyoune-Sakia El Hamra", 70),
        Map.entry("Dakhla-Oued Ed-Dahab", 80)
    );

    // Seuil livraison gratuite par région
    private static final Map<String, Integer> SEUIL_GRATUIT = Map.ofEntries(
        Map.entry("Grand Casablanca-Settat", 1500),
        Map.entry("Casablanca", 1500),
        Map.entry("Rabat-Salé-Kénitra", 1500),
        Map.entry("Rabat", 1500),
        Map.entry("Fès-Meknès", 1500),
        Map.entry("Fès", 1500),
        Map.entry("Meknès", 1500),
        Map.entry("Marrakech-Safi", 1500),
        Map.entry("Marrakech", 1500),
        Map.entry("Tanger-Tétouan-Al Hoceïma", 1500),
        Map.entry("Tanger", 1500),
        Map.entry("Souss-Massa", 1500),
        Map.entry("Agadir", 1500),
        Map.entry("Oriental", 1500),
        Map.entry("Oujda", 1500),
        Map.entry("Béni Mellal-Khénifra", 1500),
        Map.entry("Drâa-Tafilalet", 1500),
        Map.entry("Guelmim-Oued Noun", 1500),
        Map.entry("Laâyoune-Sakia El Hamra", 1500),
        Map.entry("Dakhla-Oued Ed-Dahab", 1500)
    );

    // Délai par région (jours)
    private static final Map<String, String> DELAI = Map.ofEntries(
        Map.entry("Grand Casablanca-Settat", "2 - 3 jours"),
        Map.entry("Casablanca", "2 - 3 jours"),
        Map.entry("Rabat-Sale-Kenitra", "2 - 3 jours"),
        Map.entry("Rabat-Salé-Kénitra", "2 - 3 jours"),
        Map.entry("Rabat", "2 - 3 jours"),
        Map.entry("Fes-Meknes", "3 - 4 jours"),
        Map.entry("Fès-Meknès", "3 - 4 jours"),
        Map.entry("Fes", "3 - 4 jours"),
        Map.entry("Fès", "3 - 4 jours"),
        Map.entry("Meknes", "3 - 4 jours"),
        Map.entry("Meknès", "3 - 4 jours"),
        Map.entry("Marrakech-Safi", "3 - 4 jours"),
        Map.entry("Marrakech", "3 - 4 jours"),
        Map.entry("Tanger-Tetouan-Al Hoceima", "3 - 4 jours"),
        Map.entry("Tanger-Tétouan-Al Hoceïma", "3 - 4 jours"),
        Map.entry("Tanger", "3 - 4 jours"),
        Map.entry("Souss-Massa", "4 - 5 jours"),
        Map.entry("Agadir", "4 - 5 jours"),
        Map.entry("Oriental", "4 - 5 jours"),
        Map.entry("Oujda", "4 - 5 jours"),
        Map.entry("Beni Mellal-Khenifra", "3 - 4 jours"),
        Map.entry("Béni Mellal-Khénifra", "3 - 4 jours"),
        Map.entry("Draa-Tafilalet", "5 - 6 jours"),
        Map.entry("Drâa-Tafilalet", "5 - 6 jours"),
        Map.entry("Guelmim-Oued Noun", "5 - 7 jours"),
        Map.entry("Laayoune-Sakia El Hamra", "6 - 8 jours"),
        Map.entry("Laâyoune-Sakia El Hamra", "6 - 8 jours"),
        Map.entry("Dakhla-Oued Ed-Dahab", "7 - 10 jours")
    );

    private String findKey(Map<String, ?> map, String region) {
        if (region == null) return null;
        // Correspondance exacte
        if (map.containsKey(region)) return region;
        // Correspondance souple (ignore accents/casse)
        String normalized = region.toLowerCase()
            .replace("è", "e").replace("é", "e").replace("ê", "e")
            .replace("â", "a").replace("à", "a").replace("ô", "o")
            .replace("î", "i").replace("û", "u").replace("ï", "i");
        for (String key : map.keySet()) {
            String keyNorm = key.toLowerCase()
                .replace("è", "e").replace("é", "e").replace("ê", "e")
                .replace("â", "a").replace("à", "a").replace("ô", "o")
                .replace("î", "i").replace("û", "u").replace("ï", "i");
            if (keyNorm.equals(normalized) || keyNorm.contains(normalized) || normalized.contains(keyNorm)) {
                return key;
            }
        }
        return null;
    }

    public int getFraisLivraison(String region, double totalPanier) {
        if (region == null || region.isBlank()) return 30;
        String key = findKey(SEUIL_GRATUIT, region);
        int seuil = key != null ? SEUIL_GRATUIT.get(key) : 1500;
        String fraisKey = findKey(FRAIS, region);
        int frais = fraisKey != null ? FRAIS.get(fraisKey) : 30;
        return totalPanier >= seuil ? 0 : frais;
    }

    public int getFraisBase(String region) {
        if (region == null || region.isBlank()) return 30;
        String key = findKey(FRAIS, region);
        return key != null ? FRAIS.get(key) : 30;
    }

    public int getSeuilGratuit(String region) {
        if (region == null || region.isBlank()) return 1500;
        String key = findKey(SEUIL_GRATUIT, region);
        return key != null ? SEUIL_GRATUIT.get(key) : 1500;
    }

    public String getDelai(String region) {
        if (region == null || region.isBlank()) return "3 - 5 jours";
        String key = findKey(DELAI, region);
        return key != null ? DELAI.get(key) : "3 - 5 jours";
    }
}
