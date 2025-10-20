package co.com.foodcourt.usecase.createrestaurant;

import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.restaurant.gateways.RestaurantRepository;
import co.com.foodcourt.model.user.User;
import co.com.foodcourt.model.user.gateways.UserRepository;
import co.com.foodcourt.usecase.common.ValidationMessages;
import co.com.foodcourt.usecase.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRestaurantUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private CreateRestaurantUseCase createRestaurantUseCase;

    private Restaurant restaurant;
    private User user;
    private List<Restaurant> mockRestaurants;

    @BeforeEach
    void setUp(){
        user = User.builder()
                .userId(1L)
                .firstName("Pepe")
                .lastName("Pipo")
                .phone("+573155544545")
                .email("owner@gmail.com")
                .role("OWNER")
                .build();

        restaurant = Restaurant.builder()
                .restaurantId(1L)
                .name("restaurant")
                .address("av always viva")
                .owner(user)
                .urlLogo("url.com")
                .phone("+573155544545")
                .nit(900888777L)
                .build();

        mockRestaurants = List.of(
                Restaurant.builder().restaurantId(1L).name("El Corral").urlLogo("corral.png").build(),
                Restaurant.builder().restaurantId(2L).name("Frisby").urlLogo("frisby.png").build()
        );
    }

    @Test
    void shouldSaveRestaurantAndValidateUserLikeOwner(){

        when(userRepository.getUserById(user.getUserId())).thenReturn(user);
        when(restaurantRepository.saveRestaurant(any(Restaurant.class))).thenReturn(restaurant);
        Restaurant result = createRestaurantUseCase.saveRestaurant(restaurant);

        assertEquals("OWNER",result.getOwner().getRole());
        assertEquals("restaurant", result.getName());

        verify(userRepository,times(1)).getUserById(user.getUserId());
        verify(restaurantRepository, times(1)).saveRestaurant(restaurant);

    }

    @Test
    void shouldThrowValidationExceptionWhenInvalidPhone(){
        restaurant.setPhone("abd123");
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> createRestaurantUseCase.saveRestaurant(restaurant)
        );

        assertEquals(ValidationMessages.INVALID_PHONE.getMessage(),ex.getMessage());
        verify(userRepository, never()).getUserById(any());
        verify(restaurantRepository, never()).saveRestaurant(any());
    }

    @Test
    void shouldThrowValidationExceptionWhenInvalidNIT(){
        restaurant.setNit(null);
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> createRestaurantUseCase.saveRestaurant(restaurant)
        );

        assertEquals(ValidationMessages.INVALID_NIT.getMessage(),ex.getMessage());
        verify(userRepository, never()).getUserById(any());
        verify(restaurantRepository, never()).saveRestaurant(any());
    }

    @Test
    void shouldThrowValidationExceptionWhenInvalidName(){
        restaurant.setName("66");
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> createRestaurantUseCase.saveRestaurant(restaurant)
        );

        assertEquals(ValidationMessages.INVALID_RESTAURANT_NAME.getMessage(),ex.getMessage());
        verify(userRepository, never()).getUserById(any());
        verify(restaurantRepository, never()).saveRestaurant(any());
    }

    @Test
    void shouldThrowValidationExceptionWhenInvalidUserRol(){

        user.setRole("EMPLOYEE");
        when(userRepository.getUserById(user.getUserId())).thenReturn(user);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> createRestaurantUseCase.saveRestaurant(restaurant)
        );

        assertEquals(ValidationMessages.INVALID_ROL_OWNER_RESTAURANT.getMessage(),ex.getMessage());
        verify(userRepository, times(1)).getUserById(user.getUserId());
        verify(restaurantRepository, never()).saveRestaurant(any());
    }

    @Test
    void shouldThrowValidationExceptionWhenInvalidUserNull(){

        user.setRole("EMPLOYEE");
        when(userRepository.getUserById(user.getUserId())).thenReturn(user);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> createRestaurantUseCase.saveRestaurant(restaurant)
        );

        assertEquals(ValidationMessages.INVALID_ROL_OWNER_RESTAURANT.getMessage(),ex.getMessage());
        verify(userRepository, times(1)).getUserById(user.getUserId());
        verify(restaurantRepository, never()).saveRestaurant(any());
    }


    @Test
    void shouldThrowValidationExceptionWhenRestaurantIsNull() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> createRestaurantUseCase.saveRestaurant(null)
        );

        assertEquals(ValidationMessages.INVALID_RESTAURANT.getMessage(), ex.getMessage());

        verify(userRepository, never()).getUserById(any());
        verify(restaurantRepository, never()).saveRestaurant(any());
    }

    @Test
    void shouldThrowValidationExceptionWhenUserIsNull() {

        when(userRepository.getUserById(user.getUserId())).thenReturn(null);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> createRestaurantUseCase.saveRestaurant(restaurant)
        );

        assertEquals(ValidationMessages.INVALID_RESTAURANT.getMessage(), ex.getMessage());

        verify(userRepository, times(1)).getUserById(user.getUserId());
        verify(restaurantRepository, never()).saveRestaurant(any());
    }

    /// //////////////// FEATURE HU9//////////////////

    @Test
    void shouldReturnRestaurantsOrderedByName() {
        when(restaurantRepository.findAllOrderedByNameAsc()).thenReturn(mockRestaurants);

        List<Restaurant> result = createRestaurantUseCase.getAllRestaurants();

        assertEquals(mockRestaurants, result);
        verify(restaurantRepository, times(1)).findAllOrderedByNameAsc();
        verifyNoMoreInteractions(restaurantRepository);
    }

    @Test
    void shouldReturnEmptyListWhenRepositoryReturnsEmpty() {
        when(restaurantRepository.findAllOrderedByNameAsc()).thenReturn(List.of());

        List<Restaurant> result = createRestaurantUseCase.getAllRestaurants();

        assertEquals(0, result.size());
        verify(restaurantRepository, times(1)).findAllOrderedByNameAsc();
    }





}
