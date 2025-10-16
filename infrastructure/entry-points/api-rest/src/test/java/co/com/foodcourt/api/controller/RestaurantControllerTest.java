package co.com.foodcourt.api.controller;

import co.com.foodcourt.api.common.ErrorMessages;
import co.com.foodcourt.api.dto.CreateRestaurantRequest;
import co.com.foodcourt.api.dto.CreateRestaurantResponse;
import co.com.foodcourt.api.dto.GetAllRestaurantsData;
import co.com.foodcourt.api.dto.PageResponse;
import co.com.foodcourt.api.exception.UnauthorizedException;
import co.com.foodcourt.api.global_exception_handler.GlobalExceptionHandler;
import co.com.foodcourt.api.service.RestaurantPageableService;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.user.User;
import co.com.foodcourt.model.user.exception.ExternalServiceException;
import co.com.foodcourt.usecase.createrestaurant.CreateRestaurantUseCase;
import co.com.foodcourt.usecase.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantController.class)
@ContextConfiguration(classes = {RestaurantController.class, GlobalExceptionHandler.class})
public class RestaurantControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateRestaurantUseCase createRestaurantUseCase;

    @MockitoBean
    private RestaurantPageableService restaurantPageableService;

    private PageResponse<GetAllRestaurantsData> mockResponse;


    private CreateRestaurantRequest createRequest;
    private CreateRestaurantResponse createResponse;
    private Restaurant restaurantDomain;
    private User userDomain;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void  init(){
        createRequest = new CreateRestaurantRequest(
                "restaurant",
                "av always viva",
                1L,

                "url.com",
                "+573155544545",
                900888777L
        );

        userDomain = User.builder()
                .firstName("Pepe")
                .lastName("Pipo")
                .phone("+573155544545")
                .email("owner@gmail.com")
                .role("OWNER")
                .build();

        restaurantDomain = Restaurant.builder()
                .restaurantId(1L)
                .name("restaurant")
                .address("av always viva")
                .owner(userDomain)
                .urlLogo("url.com")
                .phone("+573155544545")
                .nit(900888777L)
                .build();

        List<GetAllRestaurantsData> restaurants = List.of(
                new GetAllRestaurantsData("El Corral", "https://cdn/logo-elcorral.png"),
                new GetAllRestaurantsData("Frisby", "https://cdn/logo-frisby.png")
        );

        mockResponse = new PageResponse<>(
                restaurants, 1, 3, 2, 1, false, false, true, true
        );
    }

    @Test
    void shouldCreateRestaurantAndReturn201() throws Exception{
        when(createRestaurantUseCase.saveRestaurant(any(Restaurant.class))).thenReturn(restaurantDomain);


        mockMvc.perform(post("/api/v1/restaurants")
                        .header("X-User-id", 10L)
                        .header("X-User-role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("restaurant"))
                .andExpect(jsonPath("$.phone").value("+573155544545"));
    }


    @Test
    void shouldHandleValidationExceptionFromHandler() throws Exception {
        when(createRestaurantUseCase.saveRestaurant(any(Restaurant.class)))
                .thenThrow(new ValidationException("Custom business validation failed"));

        mockMvc.perform(post("/api/v1/restaurants")
                        .header("X-User-id", 10L)
                        .header("X-User-role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details:").value("Custom business validation failed"));
    }

    @Test
    void shouldHandleGenericExceptionFromHandler() throws Exception {
        when(createRestaurantUseCase.saveRestaurant(any(Restaurant.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/api/v1/restaurants")
                        .header("X-User-id", 10L)
                        .header("X-User-role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error:").value("Unexpected error"));
    }

    @Test
    void shouldHandleExternalServiceExceptionFromHandler() throws Exception {
        when(createRestaurantUseCase.saveRestaurant(any(Restaurant.class)))
                .thenThrow(new ExternalServiceException("Unexpected error"));

        mockMvc.perform(post("/api/v1/restaurants")
                        .header("X-User-id", 10L)
                        .header("X-User-role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error:").value("Unexpected error"));
    }

    @Test
    void shouldHandleMethodArgumentNotValidException_WhenPhoneIsBlank() throws Exception {
        String invalidRequest = """
        {
          "name": "El Buen Sabor",
          "address": "Av. Siempre Viva 123",
          "ownerId": 1,
          "urlLogo": "https://logo.com/restaurant.png",
          "phone": "",
          "nit": 900888777
        }
        """;

        mockMvc.perform(post("/api/v1/restaurants")
                        .header("X-User-id", 10L)
                        .header("X-User-role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['error:'].phone").value("phone is required"))
                .andExpect(jsonPath("$['timestamp:']").exists());
    }

    @Test
    void shouldHandleUnauthorizedExceptionFromHandler() throws Exception {

        when(createRestaurantUseCase.saveRestaurant(any(Restaurant.class)))
                .thenThrow(new UnauthorizedException("Unauthorized"));

        mockMvc.perform(post("/api/v1/restaurants")
                        .header("X-User-id", 10L)
                        .header("X-User-role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error:").value("Unauthorized"));
    }


    /// ////////////////////////// FEATURE HU 9 ///////////////////////////////////


    @Test
    void shouldReturnRestaurantsWhenRoleIsClient() throws Exception {
        when(restaurantPageableService.getRestaurants(1, 3))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/restaurants")
                        .header("X-User-role", "CLIENT")
                        .param("page", "1")
                        .param("size", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("El Corral"))
                .andExpect(jsonPath("$.content[1].urlLogo").value("https://cdn/logo-frisby.png"));

        verify(restaurantPageableService).getRestaurants(1, 3);
    }

    @Test
    void shouldReturnUnauthorizedWhenRoleIsNotClient() throws Exception {
        mockMvc.perform(get("/api/v1/restaurants")
                        .header("X-User-role", "OWNER")
                        .param("page", "1")
                        .param("size", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error:").value("Unauthorized"))
                .andExpect(jsonPath("$.details:").value(ErrorMessages.INVALID_ROL_SHOW_RESTAURANTS.getMessage()));

        verify(restaurantPageableService, never()).getRestaurants(anyInt(), anyInt());
    }


    @Test
    void shouldReturnInternalServerErrorWhenUnexpectedExceptionOccurs() throws Exception {
        when(restaurantPageableService.getRestaurants(anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/v1/restaurants")
                        .header("X-User-role", "CLIENT")
                        .param("page", "1")
                        .param("size", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error:").value("Unexpected error"));
    }

}
