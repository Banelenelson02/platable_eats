package com.plateable.repository;
import com.plateable.model.Order;
import com.plateable.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByStatus(OrderStatus status);
}
