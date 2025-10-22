package co.com.foodcourt.jpa.mapper;

import co.com.foodcourt.jpa.entity.OrderDishEntity;
import co.com.foodcourt.model.order.OrderDish;
import co.com.foodcourt.model.plate.Dish;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;


import static org.junit.jupiter.api.Assertions.*;


class OrderDishEntityMapperTest {

    private final OrderDishEntityMapper mapper = Mappers.getMapper(OrderDishEntityMapper.class);

    @Test
    void shouldMapOrderDishDomainToEntitySuccessfully() {
        Dish dish = new Dish();
        dish.setDishId(10L);
        dish.setName("Pizza");

        OrderDish domain = new OrderDish();
        domain.setQuantity(3);
        domain.setDish(dish);

        OrderDishEntity result = mapper.toEntity(domain);

        assertNotNull(result);
        assertEquals(3, result.getQuantity());
        assertNotNull(result.getDish());
        assertEquals(10L, result.getDish().getDishId());
        assertEquals("Pizza", result.getDish().getName());
    }
}
