package co.com.foodcourt.usecase.dish;


import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.model.plate.gateways.DishRepository;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.restaurant.gateways.RestaurantRepository;
import co.com.foodcourt.model.user.User;
import co.com.foodcourt.usecase.common.ValidationMessages;
import co.com.foodcourt.usecase.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private User user;

    @BeforeEach
    void setUp(){

        user = User.builder()
                .userId(1L)
                .build();

        restaurant = Restaurant.builder()
                .restaurantId(1L)
                .owner(user)
                .build();

        dish =Dish.builder()
                .name("Pizza")
                .price(20000)
                .description("italian pizza")
                .urlImage("img.png")
                .restaurant(restaurant)
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
        long ownerId = 1L;
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> dishUseCase.saveDish(null,ownerId)
        );

        assertEquals(ValidationMessages.INVALID_DISH.getMessage(), ex.getMessage());
        verify(dishRepository, never()).save(any());
    }

    @Test
    void shouldCreateDishSuccessfullyWhenValid() {
        long restaurantId= 1L;
        long ownerId = 1L;

        when(restaurantRepository.getRestaurantById(restaurantId)).thenReturn(restaurant);
        when(dishRepository.save(any(Dish.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Dish result = dishUseCase.saveDish(dish,ownerId);

        assertTrue(result.getActive());

        verify(dishRepository,times(1)).save(any(Dish.class));

    }
}
