package com.plateable.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import java.util.Map;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CheckoutIntegrationTest {
    @Autowired TestRestTemplate rest;
    @Autowired ObjectMapper mapper;

    private Map<String, Object> payload(String itemId, int quantity) {
        return Map.of(
                "customerName", "Portfolio Demo",
                "contactNumber", "0821234567",
                "deliveryAddress", "123 Test Street, Johannesburg",
                "specialInstructions", "No onions",
                "items", List.of(Map.of("menuItemId", itemId, "quantity", quantity)));
    }

    @Test
    void canCheckoutWithoutStaffCredentialsAndUsesDatabasePrices() throws Exception {
        ResponseEntity<String> response = rest.postForEntity("/api/checkout",
                payload("M001", 2), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode body = mapper.readTree(response.getBody());
        assertThat(body.get("orderId").asText()).startsWith("ORD");
        assertThat(body.get("total").asDouble()).isEqualTo(240.0);
        assertThat(body.get("itemCount").asInt()).isEqualTo(2);

        ResponseEntity<String> storedOrder = rest.withBasicAuth("staff", "changeme")
                .getForEntity("/api/orders/" + body.get("orderId").asText(), String.class);
        assertThat(storedOrder.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(mapper.readTree(storedOrder.getBody()).get("items").size()).isEqualTo(1);
        assertThat(mapper.readTree(storedOrder.getBody()).get("deliveryAddress").asText())
                .contains("Test Street");
    }

    @Test
    void rejectsUnknownMenuItemsWithoutSavingAnOrder() {
        ResponseEntity<String> response = rest.postForEntity("/api/checkout",
                payload("DOES-NOT-EXIST", 1), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void rejectsInvalidQuantities() {
        ResponseEntity<String> response = rest.postForEntity("/api/checkout",
                payload("M001", -2), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void doesNotExposeOrdersOrReservationsToAnonymousVisitors() {
        assertThat(rest.getForEntity("/api/orders", String.class).getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(rest.getForEntity("/api/reservations", String.class).getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(rest.getForEntity("/api/menu", String.class).getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }
}
