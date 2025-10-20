package co.com.foodcourt.usecase.order;


import co.com.foodcourt.model.employee_restaurant.gateways.EmployeeRestaurantRepository;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderDish;
import co.com.foodcourt.model.order.OrderStatus;
import co.com.foodcourt.model.order.gateways.OrderRepository;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.model.plate.gateways.DishRepository;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.restaurant.gateways.RestaurantRepository;
import co.com.foodcourt.usecase.common.ValidationMessages;
import co.com.foodcourt.usecase.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private DishRepository dishRepository;

    @Mock
    private EmployeeRestaurantRepository employeeRestaurantRepository;

    @InjectMocks
    private OrderUseCase orderUseCase;

    private Restaurant restaurant;
    private Dish dish;
    private OrderDish orderDish;
    private Order orderRequest;

    @BeforeEach
    void setUp() {
        restaurant = Restaurant.builder()
                .restaurantId(1L)
                .name("Burger Place")
                .build();

        dish = Dish.builder()
                .dishId(10L)
                .name("Cheeseburger")
                .build();

        orderDish = OrderDish.builder()
                .dish(dish)
                .quantity(2)
                .build();

        orderRequest = Order.builder()
                .restaurant(restaurant)
                .orderId(101L)
                .status(OrderStatus.PENDING)
                .dishes(List.of(orderDish))
                .build();
    }


    @Test
    void shouldCreateOrderSuccessfully() {
        Long clientId = 100L;

        when(orderRepository.existsByClientIdAndStatusIn(anyLong(), anyList())).thenReturn(false);

        when(dishRepository.findByRestaurantId(1L)).thenReturn(List.of(dish));
        when(restaurantRepository.getRestaurantById(1L)).thenReturn(restaurant);
        when(orderRepository.saveOrder(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderUseCase.createOrder(orderRequest, clientId);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(restaurant, result.getRestaurant());
        verify(orderRepository).saveOrder(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenClientHasOrderInProcess() {
        Long clientId = 100L;
        when(orderRepository.existsByClientIdAndStatusIn(anyLong(), anyList())).thenReturn(true);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> orderUseCase.createOrder(orderRequest, clientId)
        );

        assertEquals(ValidationMessages.INVALID_YET_ORDER_IN_PROCESS.getMessage(), ex.getMessage());
        verify(orderRepository, never()).saveOrder(any());
    }

    @Test
    void shouldThrowExceptionWhenDishNotBelongToRestaurant() {
        Long clientId = 200L;
        when(orderRepository.existsByClientIdAndStatusIn(anyLong(), anyList())).thenReturn(false);
        when(restaurantRepository.getRestaurantById(1L)).thenReturn(restaurant);


        Dish anotherDish = Dish.builder().dishId(99L).build();
        when(dishRepository.findByRestaurantId(1L)).thenReturn(List.of(anotherDish));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> orderUseCase.createOrder(orderRequest, clientId)
        );

        assertTrue(ex.getMessage().contains("restaurant"));
        verify(orderRepository, never()).saveOrder(any());
    }

    /* Feature hu 12 */

    @Test
    void shouldReturnOrders_WhenEmployeeBelongsToRestaurant() {
        Long employeeId = 200L;
        Long restaurantId = 9L;
        String status = "PENDING";

        when(employeeRestaurantRepository.findRestaurantIdByEmployeeId(employeeId))
                .thenReturn(restaurantId);
        when(orderRepository.findByRestaurantAndStatus(restaurantId, status))
                .thenReturn(List.of(orderRequest));

        List<Order> result = orderUseCase.getAllOrderByStatus(employeeId, status);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(101L, result.getFirst().getOrderId());
        assertEquals(OrderStatus.PENDING, result.getFirst().getStatus());
        verify(employeeRestaurantRepository).findRestaurantIdByEmployeeId(employeeId);
        verify(orderRepository).findByRestaurantAndStatus(restaurantId, status);
    }

    @Test
    void shouldThrowValidationException_WhenEmployeeNotBelongsToAnyRestaurant() {
        Long employeeId = 500L;
        when(employeeRestaurantRepository.findRestaurantIdByEmployeeId(employeeId))
                .thenReturn(null);

        ValidationException ex = assertThrows(ValidationException.class, () ->
                orderUseCase.getAllOrderByStatus(employeeId, "PENDING"));

        assertEquals(ValidationMessages.INVALID_EMPLOYEE_NOT_BELONG.getMessage(), ex.getMessage());
        verify(employeeRestaurantRepository).findRestaurantIdByEmployeeId(employeeId);
        verify(orderRepository, never()).findByRestaurantAndStatus(anyLong(), anyString());
    }

    @Test
    void shouldReturnEmptyList_WhenRestaurantHasNoOrders() {
        Long employeeId = 300L;
        Long restaurantId = 8L;
        String status = "DELIVERED";

        when(employeeRestaurantRepository.findRestaurantIdByEmployeeId(employeeId))
                .thenReturn(restaurantId);
        when(orderRepository.findByRestaurantAndStatus(restaurantId, status))
                .thenReturn(List.of());

        List<Order> result = orderUseCase.getAllOrderByStatus(employeeId, status);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRestaurantRepository).findRestaurantIdByEmployeeId(employeeId);
        verify(orderRepository).findByRestaurantAndStatus(restaurantId, status);
    }

}
