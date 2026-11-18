package com.plateable.config;

import com.plateable.model.MenuItem;
import com.plateable.model.Table;
import com.plateable.repository.MenuItemRepository;
import com.plateable.repository.TableRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seed(MenuItemRepository menuRepo, TableRepository tableRepo) {
        return args -> {
            // Add missing demo menu entries without overwriting staff-edited prices.
            seedMenu(menuRepo, "M001", "Wood-fired Pizza", 120.00, "Mains");
            seedMenu(menuRepo, "M002", "Butternut Bisque", 85.00, "Mains");
            seedMenu(menuRepo, "M003", "Beef Pot Pie", 145.00, "Mains");
            seedMenu(menuRepo, "D001", "Malva Pudding", 65.00, "Desserts");
            seedMenu(menuRepo, "D002", "Caramel Tart", 70.00, "Desserts");
            seedMenu(menuRepo, "WEB001", "Alfredo Pasta", 100.00, "Mains");
            seedMenu(menuRepo, "WEB002", "Chicken & Waffles", 100.00, "Mains");
            seedMenu(menuRepo, "WEB003", "Grilled Chicken", 100.00, "Mains");
            seedMenu(menuRepo, "WEB004", "Assorted Board", 100.00, "Mains");
            seedMenu(menuRepo, "WEB005", "Meatballs & Pasta", 100.00, "Mains");
            seedMenu(menuRepo, "WEB006", "Pita Wrap", 100.00, "Mains");
            seedMenu(menuRepo, "WEB007", "French Toast Stuffing", 100.00, "Desserts");
            seedMenu(menuRepo, "WEB008", "Fluffy Pancakes", 100.00, "Desserts");
            seedMenu(menuRepo, "WEB009", "Caramelized Cinnamon Rolls", 100.00, "Desserts");
            seedMenu(menuRepo, "WEB010", "Chocolate Cake", 100.00, "Desserts");
            seedMenu(menuRepo, "WEB011", "Lava Cake", 100.00, "Desserts");
            seedMenu(menuRepo, "WEB012", "Cheesecake", 100.00, "Desserts");
            seedMenu(menuRepo, "WEB013", "Carrot Cake", 100.00, "Desserts");
            if (tableRepo.count() == 0) {
                tableRepo.save(new Table("T1", 2));
                tableRepo.save(new Table("T2", 4));
                tableRepo.save(new Table("T3", 6));
                tableRepo.save(new Table("T4", 4));
            }
        };
    }

    private void seedMenu(MenuItemRepository menuRepo, String id, String name, double price, String category) {
        if (!menuRepo.existsById(id)) {
            menuRepo.save(new MenuItem(id, name, price, category));
        }
    }
}
