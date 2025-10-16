package co.com.foodcourt.api.controller;

import co.com.foodcourt.api.dto.GetAllDishesData;
import co.com.foodcourt.api.dto.PageResponse;
import co.com.foodcourt.api.service.DishPageableService;
import co.com.foodcourt.api.service.PageableService;
import co.com.foodcourt.model.category.Category;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.usecase.dish.DishUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DishPageableServiceTest {

    @Mock
    private DishUseCase dishUseCase;

    private DishPageableService dishPageableService;

    private List<Dish> mockDishes;

    @BeforeEach
    void setUp() {
        PageableService pageableService = new PageableService();
        dishPageableService = new DishPageableService(dishUseCase, pageableService);

        Category category = Category.builder().name("FastFood").build();

        mockDishes = List.of(
                Dish.builder()
                        .name("HotDog")
                        .description("american")
                        .price(10000)
                        .category(category)
                        .build(),
                Dish.builder()
                        .name("Hamburger")
                        .description("cheese and bacon")
                        .price(25000)
                        .category(category)
                        .build()
        );
    }

    @Test
    void shouldExecuteMapperLambdaAndReturnMappedRestaurants() {

        String restaurantName = "Frisby";
        Long categoryId = 1L;

        when(dishUseCase.getAllDishesByRestaurant(restaurantName, categoryId))
                .thenReturn(mockDishes);

        PageResponse<GetAllDishesData> result = dishPageableService.getDishes(restaurantName, categoryId, 1, 10);

        assertEquals(2, result.content().size());
        assertEquals("HotDog", result.content().get(0).name());
        assertEquals("Hamburger", result.content().get(1).name());
        assertEquals("FastFood", result.content().get(0).categoryName());
        assertEquals(2, result.totalElements());
    }
}