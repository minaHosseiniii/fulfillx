package com.fulfillx.order.controller;

import com.fulfillx.order.domain.dto.CreateOrderItemRequest;
import com.fulfillx.order.domain.dto.CreateOrderRequest;
import com.fulfillx.order.domain.dto.ShippingAddressRequest;
import com.fulfillx.order.repository.OrderRepository;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateOrder() throws Exception {

        CreateOrderRequest request = new CreateOrderRequest(
                1L,
                new ShippingAddressRequest(
                        "Fatemeh",
                        "09120000000",
                        "Tehran",
                        "Tehran",
                        "Example Street",
                        "1234567890"
                ),
                "IRR",
                List.of(
                        new CreateOrderItemRequest(
                                100L,
                                "Lipstick",
                                new BigDecimal("500000"),
                                2
                        ),
                        new CreateOrderItemRequest(
                                200L,
                                "Foundation",
                                new BigDecimal("300000"),
                                1
                        )
                )
        );

        mockMvc.perform(
                        post("/api/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount.amount")
                        .value(1300000))
                .andExpect(jsonPath("$.totalAmount.currency")
                        .value("IRR"))
                .andExpect(jsonPath("$.items.length()")
                        .value(2));
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                null,
                new ShippingAddressRequest(
                        "",
                        "09120000000",
                        "Tehran",
                        "Tehran",
                        "Example Street",
                        "1234567890"
                ),
                "",
                List.of()
        );

        mockMvc.perform(
                        post("/api/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.errors.customerId")
                        .value("must not be null"))
                .andExpect(jsonPath("$.errors.currency")
                        .value("must not be blank"))
                .andExpect(jsonPath("$.errors.items")
                        .value("must not be empty"))
                .andExpect(jsonPath("$.errors['shippingAddress.recipientName']")
                        .value("must not be blank"));
    }

    @Test
    void shouldReturnBadRequestWhenQuantityIsZero()
            throws Exception {

        CreateOrderRequest request = new CreateOrderRequest(
                1L,
                new ShippingAddressRequest(
                        "Fatemeh",
                        "09120000000",
                        "Tehran",
                        "Tehran",
                        "Example Street",
                        "1234567890"
                ),
                "IRR",
                List.of(
                        new CreateOrderItemRequest(
                                100L,
                                "Lipstick",
                                new BigDecimal("500000"),
                                0
                        )
                )
        );

        mockMvc.perform(
                        post("/api/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors['items[0].quantity']")
                        .value("must be greater than 0"));
    }


}
