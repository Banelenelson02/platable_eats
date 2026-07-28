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
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OrderLifecycleIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCompleteFullOrderLifecycle() {
        TestRestTemplate staffClient = restTemplate.withBasicAuth("staff", "changeme");

        CreateOrderRequest createReq = new CreateOrderRequest("T2", "W001");
        ResponseEntity<OrderResponse> createRes = staffClient.postForEntity(
                "/api/orders", createReq, OrderResponse.class);

        assertThat(createRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createRes.getBody()).isNotNull();

        String orderId = createRes.getBody().orderId();
        assertThat(orderId).isNotNull();
        assertThat(orderId).startsWith("ORD");

        AddOrderItemRequest addReq = new AddOrderItemRequest("M001", 2, "Extra crispy");
        ResponseEntity<OrderResponse> addRes = staffClient.postForEntity(
                "/api/orders/" + orderId + "/items", addReq, OrderResponse.class);

        assertThat(addRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(addRes.getBody()).isNotNull();
        assertThat(addRes.getBody().items()).hasSize(1);
        assertThat(addRes.getBody().items().get(0).quantity()).isEqualTo(2);
        assertThat(addRes.getBody().total()).isEqualTo(240.00); // 2 x R120.00
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() {
        CreateOrderRequest createReq = new CreateOrderRequest("T2", "W001");
        ResponseEntity<String> res = restTemplate.postForEntity(
                "/api/orders", createReq, String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturnMenuWithoutAuth() {
        ResponseEntity<String> res = restTemplate.getForEntity("/api/menu", String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}