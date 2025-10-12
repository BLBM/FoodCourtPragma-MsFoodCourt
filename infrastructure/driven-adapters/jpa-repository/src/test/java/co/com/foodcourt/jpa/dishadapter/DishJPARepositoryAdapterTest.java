package co.com.foodcourt.jpa.dishadapter;



import co.com.foodcourt.jpa.entity.CategoryEntity;
import co.com.foodcourt.jpa.entity.DishEntity;
import co.com.foodcourt.jpa.entity.RestaurantEntity;
import co.com.foodcourt.model.category.Category;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.model.restaurant.Restaurant;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DishJPARepositoryAdapterTest {

    @Mock
    private DishJPARepository repository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private DishJPARepositoryAdapter adapter;


    private Dish dish;
    private DishEntity dishEntity;


    @BeforeEach
    void setUp(){
        Restaurant restaurant = Restaurant.builder().restaurantId(1L).build();
        Category category = Category.builder().categoryId(2L).build();
        RestaurantEntity restaurantEntity = RestaurantEntity.builder().restaurantId(1L).build();
        CategoryEntity categoryEntity = CategoryEntity.builder().categoryId(2L).build();

         dish = Dish.builder()
                .dishId(10L)
                .name("Cheeseburger")
                .price(25000)
                .restaurant(restaurant)
                .category(category)
                .build();

         dishEntity = DishEntity.builder()
                .dishId(10L)
                .name("Cheeseburger")
                .price(25000)
                .restaurantId(restaurantEntity)
                .categoryId(categoryEntity)
                .build();
    }

    @Test
    void shouldSaveDishCorrectly() {
        when(mapper.map(dish, DishEntity.class)).thenReturn(dishEntity);
        when(repository.save(dishEntity)).thenReturn(dishEntity);
        when(mapper.map(dishEntity, Dish.class)).thenReturn(dish);

        Dish result = adapter.save(dish);

        assertNotNull(result);
        assertEquals("Cheeseburger", result.getName());
        verify(repository).save(dishEntity);
    }

    @Test
    void shouldPropagateExceptionWhenRepositoryFails() {
        when(mapper.map(dish, DishEntity.class)).thenReturn(dishEntity);
        when(repository.save(dishEntity)).thenThrow(new DataIntegrityViolationException("FK category"));

        assertThrows(DataIntegrityViolationException.class, () -> adapter.save(dish));
    }




}
