package co.com.foodcourt.jpa.mapper;

import co.com.foodcourt.jpa.entity.CategoryEntity;
import co.com.foodcourt.jpa.entity.DishEntity;
import co.com.foodcourt.jpa.entity.RestaurantEntity;
import co.com.foodcourt.model.plate.Dish;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class DishEntityMapperTest {

    private final DishEntityMapper mapper = Mappers.getMapper(DishEntityMapper.class);

    @Test
    void shouldMapDishEntityToDomainSuccessfully() {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setRestaurantId(1L);

        CategoryEntity category = new CategoryEntity();
        category.setCategoryId(5L);

        DishEntity entity = new DishEntity();
        entity.setDishId(10L);
        entity.setName("Burger");
        entity.setPrice(25000);
        entity.setRestaurantId(restaurant);
        entity.setCategoryId(category);
        entity.setDescription("Classic burger");

        Dish result = mapper.toDomain(entity);

        assertNotNull(result);
        assertEquals(10L, result.getDishId());
        assertEquals("Burger", result.getName());
        assertEquals(25000, result.getPrice());
        assertEquals(1L, result.getRestaurant().getRestaurantId());
        assertEquals(5L, result.getCategory().getCategoryId());
        assertEquals("Classic burger", result.getDescription());
    }
}
