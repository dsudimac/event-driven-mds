package com.danijelsudimac.order.api.service.app.controller;

import com.danijelsudimac.order.api.service.app.mapper.OrderMapper;
import com.danijelsudimac.order.api.service.app.service.OrderPublisher;
import com.danijelsudimac.order.api.service.model.CreateOrderEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderPublisher orderPublisher;

    @MockitoBean
    private OrderMapper orderMapper;

    @Test
    void createOrder_ShouldReturnAccepted_WhenRequestIsValid() throws Exception {
        // Arrange
        var validOrder = """
                {
                    "orderId": "123",
                    "itemId": "item-1",
                    "quantity": 5
                }
                """;

        Mockito.doNothing().when(orderPublisher).publish(any(CreateOrderEvent.class));

        // Act & Assert
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrder))
                .andExpect(status().isAccepted());
    }

    @Test
    void createOrder_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        // Arrange
        var invalidOrder = """
                {
                    "orderId": "",
                    "itemId": "item-1",
                    "quantity": -5,
                    "receivedAt": "2023-10-20T10:15:30Z"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidOrder))
                .andExpect(status().isBadRequest());
    }
}