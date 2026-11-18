package com.plateable.service;

import com.plateable.dto.request.CheckoutRequest;
import com.plateable.dto.response.CheckoutResponse;
import com.plateable.exception.ResourceNotFoundException;
import com.plateable.model.MenuItem;
import com.plateable.model.Order;
import com.plateable.repository.MenuItemRepository;
import com.plateable.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class CheckoutService {
    private final MenuItemRepository menuRepository;
    private final OrderRepository orderRepository;

    public CheckoutService(MenuItemRepository menuRepository, OrderRepository orderRepository) {
        this.menuRepository = menuRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request) {
        Order order = new Order("ORD" + UUID.randomUUID().toString().replace("-", ""),
                "DELIVERY", "WEB_ORDER");
        order.setDeliveryDetails(request.customerName(), request.contactNumber(),
                request.deliveryAddress(), request.specialInstructions());

        // The client sends only IDs and quantities. Prices are read from the database.
        for (CheckoutRequest.CheckoutItem line : request.items()) {
            MenuItem item = menuRepository.findById(line.menuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Menu item " + line.menuItemId() + " not found"));
            if (!item.isAvailable()) {
                throw new IllegalArgumentException(item.getName() + " is currently unavailable");
            }
            order.addItem(item, line.quantity(), request.specialInstructions());
        }
        orderRepository.save(order);
        int itemCount = request.items().stream().mapToInt(CheckoutRequest.CheckoutItem::quantity).sum();
        return new CheckoutResponse(order.getOrderId(), order.getStatus().name(), order.getTotal(), itemCount);
    }
}
