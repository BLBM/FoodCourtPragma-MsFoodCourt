package co.com.foodcourt.usecase.dish;


import co.com.foodcourt.model.category.Category;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.model.plate.exception.DishNotFoundException;
import co.com.foodcourt.model.plate.gateways.DishRepository;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.restaurant.gateways.RestaurantRepository;
import co.com.foodcourt.model.user.User;
import co.com.foodcourt.usecase.common.ValidationMessages;
import co.com.foodcourt.usecase.exception.ValidationException;
import co.com.foodcourt.usecase.util.ValidateDish;
import co.com.foodcourt.usecase.util.ValidateUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DishUseCaseTest {

    @Mock
    DishRepository dishRepository;

    @Mock
    RestaurantRepository restaurantRepository;

    @InjectMocks
    DishUseCase dishUseCase;

    private Restaurant restaurant;
    private Dish dish;
    private Category category;
    private Dish partialDish;



    @BeforeEach
    void setUp(){

        User user = User.builder()
                .userId(10L)
                .build();

        restaurant = Restaurant.builder()
                .restaurantId(1L)
                .owner(user)
                .build();

        category = Category.builder()
                .categoryId(1L)
                .build();

        dish =Dish.builder()
                .name("Pizza")
                .price(20000)
                .description("italian pizza")
                .urlImage("img.png")
                .restaurant(restaurant)
                .category(category)
                .active(true)
                .build();

        partialDish = Dish.builder()
                .price(30000)
                .description("italian pizza with salami")
                .active(true)
                .build();

    }


    @Test
    void shouldThrowExceptionWhenOwnerIsNotRestaurantOwner(){
        long restaurantId= 1L;
        long ownerId = 99L;

        when(restaurantRepository.getRestaurantById(restaurantId)).thenReturn(restaurant);

        ValidationException ex= assertThrows(ValidationException.class,
             ()-> dishUseCase.saveDish(dish,ownerId));

        assertEquals(ValidationMessages.INVALID_OWNER_OF_RESTAURANT.getMessage(),ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDishPriceEqualsZero(){
        dish.setPrice(0);
        long ownerId = 1L;

        ValidationException ex= assertThrows(ValidationException.class,
                ()-> dishUseCase.saveDish(dish,ownerId));

        assertEquals(ValidationMessages.INVALID_DISH_PRICE.getMessage(),ex.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionWhenRestaurantIsNull() {
        long ownerId = 10L;
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> dishUseCase.saveDish(null,ownerId)
        );

        assertEquals(ValidationMessages.INVALID_DISH.getMessage(), ex.getMessage());
        verify(dishRepository, never()).saveDish(any());
    }

    @Test
    void shouldCreateDishSuccessfullyWhenValid() {
        long restaurantId= 1L;
        long ownerId = 10L;

        when(restaurantRepository.getRestaurantById(restaurantId)).thenReturn(restaurant);
        when(dishRepository.saveDish(any(Dish.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));


        Dish result = dishUseCase.saveDish(dish,ownerId);

        assertTrue(result.getActive());
        assertEquals(restaurant, result.getRestaurant());
        assertEquals(category.getCategoryId(), result.getCategory().getCategoryId());

        verify(dishRepository,times(1)).saveDish(any(Dish.class));

    }

    /// //////////////////////// FEATURE HU4 UPDATED DISH////////////////////////

    @Test
    void ShouldUpdateDishSuccessfullyWhenValid() {
        long restaurantId = 1L;
        long dishId = 1L;
        long ownerId = 10L;



        when(dishRepository.findById(dishId)).thenReturn(dish);
        when(restaurantRepository.getRestaurantById(restaurantId)).thenReturn(restaurant);
        when(dishRepository.saveDish(any(Dish.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Dish updated = dishUseCase.updateDish(dishId, partialDish, ownerId);

        assertNotNull(updated);
        assertEquals(partialDish.getPrice(), updated.getPrice());
        assertEquals(partialDish.getDescription(), updated.getDescription());

        verify(dishRepository).findById(dishId);
        verify(restaurantRepository).getRestaurantById(restaurantId);
        verify(dishRepository).saveDish(any(Dish.class));

    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenOwnerIsInvalid() {
        try (MockedStatic<ValidateUser> userValidator = mockStatic(ValidateUser.class)) {
            when(dishRepository.findById(1L)).thenReturn(dish);
            when(restaurantRepository.getRestaurantById(1L)).thenReturn(restaurant);

            userValidator.when(() ->
                    ValidateUser.validateOwner(99L, 10L)
            ).thenThrow(new ValidationException("Invalid owner"));

            assertThrows(ValidationException.class,
                    () -> dishUseCase.updateDish(1L, partialDish, 99L));

            userValidator.verify(() -> ValidateUser.validateOwner(99L, 10L));
        }
    }

    @Test
    void shouldThrowValidationExceptionWhenPriceInvalid() {
        try (MockedStatic<ValidateDish> dishValidator = mockStatic(ValidateDish.class)) {
            partialDish.setPrice(-1000);

            when(dishRepository.findById(1L)).thenReturn(dish);
            when(restaurantRepository.getRestaurantById(1L)).thenReturn(restaurant);

            dishValidator.when(() -> ValidateDish.validatePrice(-1000))
                    .thenThrow(new ValidationException("Invalid price"));

            assertThrows(ValidationException.class,
                    () -> dishUseCase.updateDish(1L, partialDish, 10L));
        }
    }


    /// ////////// FEATURE HU7/////////////////////////


    @Test
    void shouldNotUpdateFieldsWhenNullValuesProvided() {
        Dish partialDish = Dish.builder()
                .price(null)
                .description(null)
                .active(null)
                .build();

        when(dishRepository.findById(1L)).thenReturn(dish);
        when(restaurantRepository.getRestaurantById(1L)).thenReturn(restaurant);
        when(dishRepository.saveDish(any(Dish.class))).thenAnswer(inv -> inv.getArgument(0));

        Dish result = dishUseCase.updateDish(1L, partialDish, 10L);

        assertEquals(20000, result.getPrice()); // no cambió
        assertEquals("italian pizza", result.getDescription());
        assertTrue(result.getActive());
        verify(dishRepository).saveDish(result);
    }

    /// ////////// FEATURE HU10 - getAllDishesByRestaurant /////////////////////////

    @Test
    void shouldGetAllDishesByRestaurantNameSuccessfully() {
        String restaurantName = "El Corral";
        List<Dish> dishes = List.of(dish);
        when(dishRepository.findByRestaurantName(restaurantName)).thenReturn(dishes);

        List<Dish> result = dishUseCase.getAllDishesByRestaurant(restaurantName, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pizza", result.getFirst().getName());
        verify(dishRepository).findByRestaurantName(restaurantName);
        verify(dishRepository, never()).findByRestaurantNameAndCategoryId(anyString(), anyLong());
    }

    @Test
    void shouldGetAllDishesByRestaurantNameAndCategoryIdSuccessfully() {
        String restaurantName = "El Corral";
        Long categoryId = 1L;
        List<Dish> dishes = List.of(dish);
        when(dishRepository.findByRestaurantNameAndCategoryId(restaurantName, categoryId)).thenReturn(dishes);

        List<Dish> result = dishUseCase.getAllDishesByRestaurant(restaurantName, categoryId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pizza", result.getFirst().getName());
        verify(dishRepository).findByRestaurantNameAndCategoryId(restaurantName, categoryId);
        verify(dishRepository, never()).findByRestaurantName(anyString());
    }

    @Test
    void shouldThrowExceptionWhenRestaurantNameIsNull() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> dishUseCase.getAllDishesByRestaurant(null, 1L)
        );

        assertEquals(ValidationMessages.INVALID_RESTAURANT_PARAM.getMessage(), ex.getMessage());
        verify(dishRepository, never()).findByRestaurantName(anyString());
        verify(dishRepository, never()).findByRestaurantNameAndCategoryId(anyString(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenRestaurantNameIsBlank() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> dishUseCase.getAllDishesByRestaurant("   ", 1L)
        );

        assertEquals(ValidationMessages.INVALID_RESTAURANT_PARAM.getMessage(), ex.getMessage());
        verify(dishRepository, never()).findByRestaurantName(anyString());
    }

    @Test
    void shouldThrowExceptionWhenNoDishesFoundByRestaurantName() {
        String restaurantName = "NonExistent";
        when(dishRepository.findByRestaurantName(restaurantName)).thenReturn(List.of());

        DishNotFoundException ex = assertThrows(
                DishNotFoundException.class,
                () -> dishUseCase.getAllDishesByRestaurant(restaurantName, null)
        );

        assertTrue(ex.getMessage().contains(restaurantName));
        verify(dishRepository).findByRestaurantName(restaurantName);
    }

    @Test
    void shouldThrowExceptionWhenNoDishesFoundByRestaurantNameAndCategory() {
        String restaurantName = "El Corral";
        Long categoryId = 99L;
        when(dishRepository.findByRestaurantNameAndCategoryId(restaurantName, categoryId)).thenReturn(List.of());

        DishNotFoundException ex = assertThrows(
                DishNotFoundException.class,
                () -> dishUseCase.getAllDishesByRestaurant(restaurantName, categoryId)
        );

        assertTrue(ex.getMessage().contains(restaurantName));
        verify(dishRepository).findByRestaurantNameAndCategoryId(restaurantName, categoryId);
    }



}
