package co.com.foodcourt.jpa.dishadapter;



import co.com.foodcourt.jpa.common.ErrorConstants;
import co.com.foodcourt.jpa.entity.CategoryEntity;
import co.com.foodcourt.jpa.entity.DishEntity;
import co.com.foodcourt.jpa.entity.RestaurantEntity;
import co.com.foodcourt.jpa.mapper.DishEntityMapper;
import co.com.foodcourt.model.category.Category;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.model.plate.exception.DishNotFoundException;
import co.com.foodcourt.model.restaurant.Restaurant;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishJPARepositoryAdapterTest {

    @Mock
    private DishJPARepository repository;

    @Mock
    private ObjectMapper mapper;

    @Spy
    private DishEntityMapper dishEntityMapper;

    @InjectMocks
    private DishJPARepositoryAdapter adapter;


    private Dish dish;
    private DishEntity dishEntity;


    @BeforeEach
    void setUp(){
        Restaurant restaurant = Restaurant.builder().restaurantId(1L).build();
        Category category = Category.builder().categoryId(2L).build();
        RestaurantEntity restaurantEntity = RestaurantEntity.builder().restaurantId(1L).name("El Corral").build();
        CategoryEntity categoryEntity = CategoryEntity.builder().categoryId(2L).name("FastFood").build();

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

        Dish result = adapter.saveDish(dish);

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

    /// ///////////////// FEATURE  HU4 //////////////////////////


    @Test
    void shouldFindDishByIdSuccessfully() {
        when(repository.findById(1L)).thenReturn(Optional.of(dishEntity));
        when(dishEntityMapper.toDomain(dishEntity)).thenReturn(dish);

        Dish result = adapter.findById(1L);

        assertNotNull(result);
        assertEquals(10L, result.getDishId());
        assertEquals("Cheeseburger", result.getName());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowDishNotFoundExceptionWhenDishDoesNotExist() {

        when(repository.findById(99L)).thenReturn(Optional.empty());

        DishNotFoundException exception = assertThrows(
                DishNotFoundException.class,
                () -> adapter.findById(99L)
        );

        assertEquals(ErrorConstants.DISH_NOT_FOUND.getMessage(), exception.getMessage());
        verify(repository, times(1)).findById(99L);
    }

    /// ///////////////////////// FEATURE HU 10////////////////////////////////

    @Test
    void shouldFindDishesByRestaurantNameSuccessfully() {
        long restaurantId = 1L;

        List<DishEntity> dishEntities = List.of(dishEntity);
        when(repository.findByRestaurantId(restaurantId)).thenReturn(dishEntities);
        when(mapper.map(dishEntity, Dish.class)).thenReturn(dish);
        when(mapper.map(dishEntity.getCategoryId(), Category.class)).thenReturn(dish.getCategory());
        when(mapper.map(dishEntity.getRestaurantId(), Restaurant.class)).thenReturn(dish.getRestaurant());

        List<Dish> result = adapter.findByRestaurantId(restaurantId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Cheeseburger", result.getFirst().getName());
        assertNotNull(result.getFirst().getCategory());
        assertNotNull(result.getFirst().getRestaurant());
        verify(repository).findByRestaurantId(restaurantId);
    }

    @Test
    void shouldReturnEmptyListWhenNoDishesByRestaurantName() {
        long restaurantId = 1L;

        when(repository.findByRestaurantId(restaurantId)).thenReturn(List.of());

        List<Dish> result = adapter.findByRestaurantId(restaurantId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindDishesByRestaurantNameAndCategoryIdSuccessfully() {
        long restaurantId = 1L;

        List<DishEntity> dishEntities = List.of(dishEntity);
        when(repository.findByRestaurantIdAndCategoryId(restaurantId, 2L)).thenReturn(dishEntities);
        when(mapper.map(dishEntity, Dish.class)).thenReturn(dish);
        when(mapper.map(dishEntity.getCategoryId(), Category.class)).thenReturn(dish.getCategory());
        when(mapper.map(dishEntity.getRestaurantId(), Restaurant.class)).thenReturn(dish.getRestaurant());

        List<Dish> result = adapter.findByRestaurantIdAndCategoryId(restaurantId, 2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Cheeseburger", result.getFirst().getName());
        assertEquals(2L, result.getFirst().getCategory().getCategoryId());
        verify(repository).findByRestaurantIdAndCategoryId(restaurantId, 2L);
    }

    @Test
    void shouldReturnEmptyListWhenNoDishesByRestaurantNameAndCategoryId() {
        long restaurantId = 1L;

        when(repository.findByRestaurantIdAndCategoryId(restaurantId, 99L)).thenReturn(List.of());

        List<Dish> result = adapter.findByRestaurantIdAndCategoryId(restaurantId, 99L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}
