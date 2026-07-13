package com.plateable.integration;

import com.plateable.dto.request.AddOrderItemRequest;
import com.plateable.dto.request.CreateOrderRequest;
import com.plateable.dto.response.OrderResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderLifecycleIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCompleteFullOrderLifecycle() {
        // 1. Authenticate using the basic auth we just configured
        TestRestTemplate staffClient = restTemplate.withBasicAuth("staff", "changeme");

        // 2. Create a new order for Table T2
        CreateOrderRequest createReq = new CreateOrderRequest("T2", "W001");
        ResponseEntity<OrderResponse> createRes = staffClient.postForEntity("/api/orders", createReq, OrderResponse.class);
        
        assertThat(createRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String orderId = createRes.getBody().orderId();
        assertThat(orderId).isNotNull();

        // 3. Add 2 Wood-fired Pizzas (M001) to the order
        AddOrderItemRequest addReq = new AddOrderItemRequest("M001", 2, "Extra crispy");
        ResponseEntity<OrderResponse> addRes = staffClient.postForEntity("/api/orders/" + orderId + "/items", addReq, OrderResponse.class);
        
        assertThat(addRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(addRes.getBody().items()).hasSize(1);
        assertThat(addRes.getBody().items().get(0).quantity()).isEqualTo(2);
        assertThat(addRes.getBody().total()).isEqualTo(240.00); // 2 * R120.00
    }
}
