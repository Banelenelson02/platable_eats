package com.plateable.config;
import com.plateable.model.MenuItem;
import com.plateable.model.Table;
import com.plateable.repository.MenuItemRepository;
import com.plateable.repository.TableRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {
    @org.springframework.context.annotation.Bean
    CommandLineRunner seed(MenuItemRepository menuRepo, TableRepository tableRepo) {
        return args -> {
            if (menuRepo.count() == 0) {
                menuRepo.save(new MenuItem("M001", "Wood-fired Pizza", 120.00, "Mains"));
                menuRepo.save(new MenuItem("M002", "Butternut Bisque", 85.00, "Mains"));
                menuRepo.save(new MenuItem("M003", "Beef Pot Pie", 145.00, "Mains"));
                menuRepo.save(new MenuItem("D001", "Malva Pudding", 65.00, "Desserts"));
                menuRepo.save(new MenuItem("D002", "Caramel Tart", 70.00, "Desserts"));
            }
            if (tableRepo.count() == 0) {
                tableRepo.save(new Table("T1", 2));
                tableRepo.save(new Table("T2", 4));
                tableRepo.save(new Table("T3", 6));
                tableRepo.save(new Table("T4", 4));
            }
        };
    }
}
