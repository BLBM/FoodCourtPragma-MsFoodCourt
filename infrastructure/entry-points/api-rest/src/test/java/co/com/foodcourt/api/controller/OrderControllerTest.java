package co.com.foodcourt.api.controller;


import co.com.foodcourt.api.dto.CreateOrderRequest;
import co.com.foodcourt.api.global_exception_handler.GlobalExceptionHandler;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderDish;
import co.com.foodcourt.model.order.OrderStatus;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.restaurant.exception.RestaurantNotFoundException;
import co.com.foodcourt.usecase.order.OrderUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {OrderController.class, GlobalExceptionHandler.class})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderUseCase orderUseCase;

    private static final String BASE_URL = "/api/v1/orders";
    private static final Long CLIENT_ID = 500L;
    private static final String CLIENT_ROLE = "CLIENT";
    private static final String OWNER_ROLE = "OWNER";

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                1L,
                List.of(new CreateOrderRequest.DishRequest(10L, 2))
        );

        Order mockOrder = Order.builder()
                .orderId(99L)
                .restaurant(Restaurant.builder().restaurantId(1L).build())
                .clientId(CLIENT_ID)
                .status(OrderStatus.PENDING)
                .creationDate(LocalDateTime.now())
                .dishes(List.of(OrderDish.builder()
                        .dish(Dish.builder().dishId(10L).build())
                        .quantity(2)
                        .build()))
                .build();

        when(orderUseCase.createOrder(any(Order.class), eq(CLIENT_ID)))
                .thenReturn(mockOrder);

        mockMvc.perform(post(BASE_URL)
                        .header("X-User-id", CLIENT_ID)
                        .header("X-User-role", CLIENT_ROLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(99))
                .andExpect(jsonPath("$.restaurantId").value(1))
                .andExpect(jsonPath("$.clientId").value(500))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.creationDate").exists());

        verify(orderUseCase).createOrder(any(Order.class), eq(CLIENT_ID));
    }

    @Test
    void shouldThrowUnauthorizedWhenRoleIsNotClient() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                1L,
                List.of(new CreateOrderRequest.DishRequest(10L, 2))
        );

        mockMvc.perform(post(BASE_URL)
                        .header("X-User-id", CLIENT_ID)
                        .header("X-User-role", OWNER_ROLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error:").value("Unauthorized"));

        verify(orderUseCase, never()).createOrder(any(), anyLong());
    }

    @Test
    void shouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        String invalidJson = """
                {
                    "restaurantId": null,
                    "dishes": []
                }
                """;

        mockMvc.perform(post(BASE_URL)
                        .header("X-User-id", CLIENT_ID)
                        .header("X-User-role", CLIENT_ROLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(orderUseCase, never()).createOrder(any(), anyLong());
    }


    @Test
    void shouldReturnNotFoundWhenNoDishesAvailable() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                1L,
                List.of(new CreateOrderRequest.DishRequest(10L, 2))
        );

        when(orderUseCase.createOrder(any(Order.class), eq(CLIENT_ID)))
                .thenThrow(new RestaurantNotFoundException("Not found Restaurant"));

        mockMvc.perform(post(BASE_URL)
                        .header("X-User-id", CLIENT_ID)
                        .header("X-User-role", CLIENT_ROLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error:").value("Business validation error"))
                .andExpect(jsonPath("$.details:").value("Not found Restaurant"));
    }
}

