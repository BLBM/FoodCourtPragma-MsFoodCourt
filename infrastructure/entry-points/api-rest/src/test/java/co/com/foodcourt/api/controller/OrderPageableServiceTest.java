package co.com.foodcourt.api.controller;

import co.com.foodcourt.api.dto.GetAllOrdersData;
import co.com.foodcourt.api.dto.OrderDishData;
import co.com.foodcourt.api.dto.PageResponse;
import co.com.foodcourt.api.service.OrderPageableService;
import co.com.foodcourt.api.service.PageableService;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderDish;
import co.com.foodcourt.model.order.OrderStatus;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.usecase.create_order.CreateOrderUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderPageableServiceTest {

    @Mock
    private CreateOrderUseCase createOrderUseCase;

    @InjectMocks
    private OrderPageableService orderPageableService;

    @BeforeEach
    void setUp() {
        PageableService pageableService = new PageableService();
        orderPageableService = new OrderPageableService(createOrderUseCase, pageableService);
    }

    @Test
    void shouldPaginateAndMapOrdersSuccessfully() {
        Long employeeId = 10L;
        String status = "PENDING";

        OrderDish dish1 = OrderDish.builder()
                .dish(Dish.builder().dishId(1L).name("PipoBurger Classic").build())
                .quantity(2)
                .build();

        OrderDish dish2 = OrderDish.builder()
                .dish(Dish.builder().dishId(2L).name("French Fries").build())
                .quantity(1)
                .build();

        Restaurant restaurant = Restaurant.builder()
                .restaurantId(9L)
                .name("PipoBurger")
                .build();

        Order order = Order.builder()
                .orderId(100L)
                .clientId(501L)
                .restaurant(restaurant)
                .creationDate(LocalDateTime.of(2025, 10, 20, 12, 30))
                .status(OrderStatus.PENDING)
                .dishes(List.of(dish1, dish2))
                .build();

        when(createOrderUseCase.getAllOrderByStatus(employeeId, status))
                .thenReturn(List.of(order));

        PageResponse<GetAllOrdersData> response =
                orderPageableService.getOrders(employeeId, status, 1, 5);

        assertNotNull(response);
        assertEquals(1, response.content().size());
        GetAllOrdersData orderData = response.content().getFirst();

        assertEquals(100L, orderData.orderId());
        assertEquals(501L, orderData.clientId());
        assertEquals("PipoBurger", orderData.restaurantName());
        assertEquals(OrderStatus.PENDING, orderData.status());
        assertEquals(2, orderData.dishes().size());

        OrderDishData dishData = orderData.dishes().getFirst();
        assertEquals(1L, dishData.dishId());
        assertEquals("PipoBurger Classic", dishData.dishName());
        assertEquals(2, dishData.quantity());

        verify(createOrderUseCase, times(1))
                .getAllOrderByStatus(employeeId, status);
    }

    @Test
    void shouldReturnEmptyPage_WhenNoOrdersFound() {
        when(createOrderUseCase.getAllOrderByStatus(anyLong(), anyString()))
                .thenReturn(List.of());

        PageResponse<GetAllOrdersData> response =
                orderPageableService.getOrders(10L, "DELIVERED", 1, 5);

        assertNotNull(response);
        assertTrue(response.content().isEmpty());
        assertEquals(0, response.totalElements());
        assertEquals(0, response.totalPages());
        verify(createOrderUseCase).getAllOrderByStatus(10L, "DELIVERED");
    }
}
