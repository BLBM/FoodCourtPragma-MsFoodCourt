package co.com.foodcourt.usecase.employee_restaurant;

import co.com.foodcourt.model.employee_restaurant.EmployeeRestaurant;
import co.com.foodcourt.model.employee_restaurant.gateways.EmployeeRestaurantRepository;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.restaurant.gateways.RestaurantRepository;
import co.com.foodcourt.model.user.User;
import co.com.foodcourt.model.user.gateways.UserRepository;
import co.com.foodcourt.usecase.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeRestaurantUseCaseTest {

    @Mock
    private EmployeeRestaurantRepository repository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmployeeRestaurantUseCase useCase;

    private static final String ROL_EMPLOYEE = "EMPLOYEE";
    private static final String ROL_OWNER = "OWNER";
    private static final Long EMPLOYEE_ID = 1L;
    private static final Long RESTAURANT_ID = 100L;
    private static final Long OWNER_ID = 50L;

    private Restaurant restaurant;
    private User owner;
    private User employee;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .userId(OWNER_ID)
                .role(ROL_OWNER)
                .build();

        restaurant = Restaurant.builder()
                .restaurantId(RESTAURANT_ID)
                .owner(owner)
                .build();

        employee = User.builder()
                .userId(EMPLOYEE_ID)
                .role(ROL_EMPLOYEE)
                .build();
    }

    @Test
    void assign_shouldAssignEmployeeSuccessfully() {
        when(restaurantRepository.getRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(userRepository.getUserById(EMPLOYEE_ID)).thenReturn(employee);

        useCase.assign(EMPLOYEE_ID, RESTAURANT_ID, OWNER_ID);

        verify(restaurantRepository).getRestaurantById(RESTAURANT_ID);
        verify(userRepository).getUserById(EMPLOYEE_ID);

        ArgumentCaptor<EmployeeRestaurant> captor = ArgumentCaptor.forClass(EmployeeRestaurant.class);
        verify(repository).saveEmployeeRestaurant(captor.capture());

        EmployeeRestaurant savedEmployeeRestaurant = captor.getValue();
        assertEquals(EMPLOYEE_ID, savedEmployeeRestaurant.getEmployeeId());
        assertEquals(RESTAURANT_ID, savedEmployeeRestaurant.getRestaurantId());
    }

    @Test
    void assign_shouldThrowExceptionWhenOwnerIsInvalid() {
        Long invalidOwnerId = 999L;
        when(restaurantRepository.getRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);

        ValidationException exception = assertThrows(ValidationException.class, () ->
                useCase.assign(EMPLOYEE_ID, RESTAURANT_ID, invalidOwnerId)
        );

        verify(restaurantRepository).getRestaurantById(RESTAURANT_ID);
        verify(userRepository, never()).getUserById(any());
        verify(repository, never()).saveEmployeeRestaurant(any());
    }

    @Test
    void assign_shouldThrowExceptionWhenUserIsNotEmployee() {
        User nonEmployee = User.builder()
                .userId(EMPLOYEE_ID)
                .role("CUSTOMER")
                .build();

        when(restaurantRepository.getRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(userRepository.getUserById(EMPLOYEE_ID)).thenReturn(nonEmployee);

        ValidationException exception = assertThrows(ValidationException.class, () ->
                useCase.assign(EMPLOYEE_ID, RESTAURANT_ID, OWNER_ID)
        );

        verify(restaurantRepository).getRestaurantById(RESTAURANT_ID);
        verify(userRepository).getUserById(EMPLOYEE_ID);
        verify(repository, never()).saveEmployeeRestaurant(any());
    }


    @Test
    void assign_shouldCallAllRepositoriesInCorrectOrder() {
        when(restaurantRepository.getRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(userRepository.getUserById(EMPLOYEE_ID)).thenReturn(employee);

        useCase.assign(EMPLOYEE_ID, RESTAURANT_ID, OWNER_ID);

        var inOrder = inOrder(restaurantRepository, userRepository, repository);
        inOrder.verify(restaurantRepository).getRestaurantById(RESTAURANT_ID);
        inOrder.verify(userRepository).getUserById(EMPLOYEE_ID);
        inOrder.verify(repository).saveEmployeeRestaurant(any(EmployeeRestaurant.class));
    }

    @Test
    void assign_shouldThrowExceptionWhenOwnerIdDoesNotMatch() {
        User differentOwner = User.builder()
                .userId(999L)
                .role(ROL_OWNER)
                .build();

        Restaurant restaurantWithDifferentOwner = Restaurant.builder()
                .restaurantId(RESTAURANT_ID)
                .owner(differentOwner)
                .build();

        when(restaurantRepository.getRestaurantById(RESTAURANT_ID))
                .thenReturn(restaurantWithDifferentOwner);

        assertThrows(ValidationException.class, () ->
                useCase.assign(EMPLOYEE_ID, RESTAURANT_ID, OWNER_ID)
        );

        verify(repository, never()).saveEmployeeRestaurant(any());
    }
}