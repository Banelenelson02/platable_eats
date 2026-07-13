package com.plateable.repository;
import com.plateable.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MenuItemRepository extends JpaRepository<MenuItem, String> {}
