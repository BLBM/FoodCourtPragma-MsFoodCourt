package co.com.foodcourt.jpa.mapper;

import co.com.foodcourt.jpa.entity.*;
import co.com.foodcourt.model.order.*;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.model.restaurant.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderEntityMapperTest {

    private OrderEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = MapperTestUtil.createMapperWithDependencies(OrderEntityMapper.class);
    }

    @Test
    void shouldMapOrderEntityToDomainSuccessfully() {
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setRestaurantId(1L);
        restaurantEntity.setName("Burger Place");

        DishEntity dishEntity = new DishEntity();
        dishEntity.setDishId(10L);
        dishEntity.setName("Double Burger");

        OrderDishEntity orderDishEntity = new OrderDishEntity();
        orderDishEntity.setDish(dishEntity);
        orderDishEntity.setQuantity(2);

        OrderEntity entity = new OrderEntity();
        entity.setOrderId(100L);
        entity.setClientId(500L);
        entity.setRestaurant(restaurantEntity);
        entity.setChefId(30L);
        entity.setOrderDishes(List.of(orderDishEntity));
        entity.setStatus(OrderStatusEntity.PENDING);
        entity.setCreationDate(LocalDateTime.now());

        Order result = mapper.toDomain(entity);

        assertNotNull(result);
        assertEquals(100L, result.getOrderId());
        assertEquals(500L, result.getClientId());
        assertEquals(30L, result.getChef());
        assertNotNull(result.getRestaurant());
        assertEquals("Burger Place", result.getRestaurant().getName());
        assertEquals("Double Burger", result.getDishes().get(0).getDish().getName());
    }

    @Test
    void shouldMapOrderDomainToEntitySuccessfully() {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(1L);
        restaurant.setName("Burger Place");

        Dish dish = new Dish();
        dish.setDishId(10L);
        dish.setName("Double Burger");

        OrderDish orderDish = new OrderDish();
        orderDish.setQuantity(2);
        orderDish.setDish(dish);

        Order domain = new Order();
        domain.setOrderId(100L);
        domain.setClientId(500L);
        domain.setChef(30L);
        domain.setRestaurant(restaurant);
        domain.setDishes(List.of(orderDish));
        domain.setStatus(OrderStatus.READY);

        OrderEntity result = mapper.toEntity(domain);

        assertNotNull(result);
        assertEquals(100L, result.getOrderId());
        assertEquals(500L, result.getClientId());
        assertEquals(30L, result.getChefId());
        assertNotNull(result.getRestaurant());
        assertEquals("Burger Place", result.getRestaurant().getName());
        assertEquals("Double Burger", result.getOrderDishes().getFirst().getDish().getName());
    }

    @Test
    void shouldMapStatusBetweenDomainAndEntityCorrectly() {
        assertEquals(OrderStatusEntity.PENDING, mapper.toEntityStatus(OrderStatus.PENDING));
        assertEquals(OrderStatus.PENDING, mapper.toDomainStatus(OrderStatusEntity.PENDING));
        assertEquals(2, mapper.toEntityStatusList(List.of(OrderStatus.PENDING, OrderStatus.READY)).size());
    }
}
