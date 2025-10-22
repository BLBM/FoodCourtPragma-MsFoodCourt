package co.com.foodcourt.usecase.create_order;


import co.com.foodcourt.model.actor.Actor;
import co.com.foodcourt.model.actor.ActorRole;
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
import co.com.foodcourt.usecase.traceability_recorder.TraceabilityRecorderUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private DishRepository dishRepository;

    @Mock
    private EmployeeRestaurantRepository employeeRestaurantRepository;

    @Mock
    private TraceabilityRecorderUseCase traceabilityRecorderUseCase;

    @InjectMocks
    private CreateOrderUseCase createOrderUseCase;

    private Restaurant restaurant;
    private Dish dish;
    private Order orderRequest;
    private Actor actor;


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

        OrderDish orderDish = OrderDish.builder()
                .dish(dish)
                .quantity(2)
                .build();

        orderRequest = Order.builder()
                .restaurant(restaurant)
                .orderId(101L)
                .status(OrderStatus.PENDING)
                .dishes(List.of(orderDish))
                .build();

        actor = Actor.builder().id(1L).email("test@gmail.cm").role(ActorRole.CLIENT).build();
    }


    @Test
    void shouldCreateOrderSuccessfully() {

        when(orderRepository.existsByClientIdAndStatusIn(anyLong(), anyList())).thenReturn(false);

        when(dishRepository.findByRestaurantId(1L)).thenReturn(List.of(dish));
        when(restaurantRepository.getRestaurantById(1L)).thenReturn(restaurant);
        when(orderRepository.saveOrder(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = createOrderUseCase.createOrder(orderRequest, actor);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(restaurant, result.getRestaurant());
        verify(orderRepository).saveOrder(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenClientHasOrderInProcess() {
        when(orderRepository.existsByClientIdAndStatusIn(anyLong(), anyList())).thenReturn(true);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> createOrderUseCase.createOrder(orderRequest, actor)
        );

        assertEquals(ValidationMessages.INVALID_YET_ORDER_IN_PROCESS.getMessage(), ex.getMessage());
        verify(orderRepository, never()).saveOrder(any());
    }

    @Test
    void shouldThrowExceptionWhenDishNotBelongToRestaurant() {
        when(orderRepository.existsByClientIdAndStatusIn(anyLong(), anyList())).thenReturn(false);
        when(restaurantRepository.getRestaurantById(1L)).thenReturn(restaurant);


        Dish anotherDish = Dish.builder().dishId(99L).build();
        when(dishRepository.findByRestaurantId(1L)).thenReturn(List.of(anotherDish));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> createOrderUseCase.createOrder(orderRequest, actor)
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

        List<Order> result = createOrderUseCase.getAllOrderByStatus(employeeId, status);

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
                createOrderUseCase.getAllOrderByStatus(employeeId, "PENDING"));

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

        List<Order> result = createOrderUseCase.getAllOrderByStatus(employeeId, status);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRestaurantRepository).findRestaurantIdByEmployeeId(employeeId);
        verify(orderRepository).findByRestaurantAndStatus(restaurantId, status);
    }

}
