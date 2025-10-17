package co.com.foodcourt.jpa.order_adapter;





import co.com.foodcourt.jpa.entity.*;
import co.com.foodcourt.jpa.mapper.OrderEntityMapper;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderDish;
import co.com.foodcourt.model.order.OrderStatus;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.model.restaurant.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderJPARepositoryAdapterTest {

    @Mock
    private OrderJPARepository repository;

    @Mock
    private OrderEntityMapper mapper;

    @InjectMocks
    private OrderJPARepositoryAdapter adapter;

    private Order order;
    private OrderEntity orderEntity;

    @BeforeEach
    void setUp() {
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(5L)
                .name("Frisby")
                .build();

        RestaurantEntity restaurantEntity = RestaurantEntity.builder()
                .restaurantId(5L)
                .name("Frisby")
                .build();

        OrderDish orderDish = OrderDish.builder()
                .dish(Dish.builder().dishId(10L).name("Pizza").build())
                .quantity(2)
                .build();

        OrderDishEntity orderDishEntity = OrderDishEntity.builder()
                .dish(DishEntity.builder().dishId(10L).name("Pizza").build())
                .quantity(2)
                .build();

        order = Order.builder()
                .orderId(1L)
                .clientId(100L)
                .restaurant(restaurant)
                .dishes(List.of(orderDish))
                .status(OrderStatus.PENDING)
                .build();

        orderEntity = OrderEntity.builder()
                .orderId(1L)
                .clientId(100L)
                .restaurant(restaurantEntity)
                .orderDishes(List.of(orderDishEntity))
                .status(OrderStatusEntity.PENDING)
                .build();
    }


    @Test
    void shouldSaveOrderSuccessfully() {

        when(mapper.toEntity(order)).thenReturn(orderEntity);
        when(repository.save(orderEntity)).thenReturn(orderEntity);
        when(mapper.toDomain(orderEntity)).thenReturn(order);


        Order result = adapter.saveOrder(order);

        assertNotNull(result);
        assertEquals(order.getClientId(), result.getClientId());
        assertEquals(order.getRestaurant().getRestaurantId(), result.getRestaurant().getRestaurantId());
        assertEquals(OrderStatus.PENDING, result.getStatus());

        assertEquals(orderEntity, orderEntity.getOrderDishes().getFirst().getOrder());

        verify(mapper).toEntity(order);
        verify(repository).save(orderEntity);
        verify(mapper).toDomain(orderEntity);
    }


    @Test
    void shouldReturnTrueWhenClientHasOrdersInStatuses() {
        List<OrderStatus> statuses = List.of(OrderStatus.PENDING, OrderStatus.READY);
        List<OrderStatusEntity> statusEntities = List.of(OrderStatusEntity.PENDING, OrderStatusEntity.READY);

        when(mapper.toEntityStatusList(statuses)).thenReturn(statusEntities);
        when(repository.existsByClientIdAndStatusIn(100L, statusEntities)).thenReturn(true);

        boolean result = adapter.existsByClientIdAndStatusIn(100L, statuses);

        assertTrue(result);
        verify(mapper).toEntityStatusList(statuses);
        verify(repository).existsByClientIdAndStatusIn(100L, statusEntities);
    }
}

