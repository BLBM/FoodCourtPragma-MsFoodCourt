package co.com.foodcourt.api.controller;

import co.com.foodcourt.api.dto.CreateRestaurantRequest;
import co.com.foodcourt.api.dto.CreateRestaurantResponse;
import co.com.foodcourt.api.global_exception_handler.GlobalExceptionHandler;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantController.class)
@ContextConfiguration(classes = {RestaurantController.class, GlobalExceptionHandler.class})
public class RestaurantControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateRestaurantUseCase createRestaurantUseCase;


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
    }

    @Test
    void shouldCreateRestaurantAndReturn201() throws Exception{
        when(createRestaurantUseCase.saveRestaurant(any(Restaurant.class))).thenReturn(restaurantDomain);


        mockMvc.perform(post("/api/v1/restaurants")
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['error:'].phone").value("phone is required"))
                .andExpect(jsonPath("$['timestamp:']").exists());
    }
}
