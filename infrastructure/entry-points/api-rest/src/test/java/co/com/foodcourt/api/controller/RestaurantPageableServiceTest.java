package co.com.foodcourt.api.controller;

import co.com.foodcourt.api.dto.GetAllRestaurantsData;
import co.com.foodcourt.api.dto.PageResponse;
import co.com.foodcourt.api.service.PageableService;
import co.com.foodcourt.api.service.RestaurantPageableService;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.usecase.createrestaurant.CreateRestaurantUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantPageableServiceTest {


    @Mock
    private CreateRestaurantUseCase createRestaurantUseCase;

    private PageableService pageableService;

    @InjectMocks
    private RestaurantPageableService restaurantPageableService;

    @BeforeEach
    void setUp() {
        pageableService = new PageableService();
        restaurantPageableService = new RestaurantPageableService(createRestaurantUseCase, pageableService);
    }

    @Test
    void shouldExecuteMapperLambdaAndReturnMappedRestaurants() {

        List<Restaurant> mockRestaurants = List.of(
                Restaurant.builder().name("El Corral").urlLogo("corral.png").build(),
                Restaurant.builder().name("Frisby").urlLogo("frisby.png").build()
        );
        when(createRestaurantUseCase.execute()).thenReturn(mockRestaurants);

        PageResponse<GetAllRestaurantsData> result = restaurantPageableService.getRestaurants(1, 3);


        assertEquals(2, result.content().size());
        assertEquals("El Corral", result.content().get(0).name());
        assertEquals("Frisby", result.content().get(1).name());
    }
}