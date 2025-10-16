package co.com.foodcourt.api.controller;

import co.com.foodcourt.api.dto.CreateDishRequest;
import co.com.foodcourt.api.dto.GetAllDishesData;
import co.com.foodcourt.api.dto.PageResponse;
import co.com.foodcourt.api.dto.UpdateDishRequest;
import co.com.foodcourt.api.exception.UnauthorizedException;
import co.com.foodcourt.api.global_exception_handler.GlobalExceptionHandler;
import co.com.foodcourt.api.service.DishPageableService;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.model.plate.exception.DishNotFoundException;
import co.com.foodcourt.usecase.dish.DishUseCase;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DishController.class)
@ContextConfiguration(classes = {DishController.class, GlobalExceptionHandler.class})
public class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DishUseCase dishUseCase;

    @MockitoBean
    private DishPageableService dishPageableService;

    private CreateDishRequest createDishRequest;
    private UpdateDishRequest updateDishRequest;
    private Dish dish;



    @BeforeEach
    void setUp(){

        createDishRequest = new CreateDishRequest(
                "Cheeseburger",
                25000, 1L,
                "Delicious burger",
                "url.jpg",
                2L);

         dish = Dish.builder().name("Cheeseburger").price(25000).description("description").build();

        updateDishRequest = new UpdateDishRequest(20000, "New description",true);


    }

    @Test
    void shouldCreateDishSuccessfully() throws Exception {

        when(dishUseCase.saveDish(any(), anyLong())).thenReturn(dish);

        mockMvc.perform(post("/api/v1/dishes")
                .header("X-User-id", 10L)
                .header("X-User-role", "OWNER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDishRequest))
                )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Cheeseburger"));
    }


    @Test
    void shouldHandleValidationExceptionFromHandler() throws Exception {

        when(dishUseCase.saveDish(any(Dish.class), anyLong()))
                .thenThrow(new ValidationException("Custom business validation failed"));

        mockMvc.perform(post("/api/v1/dishes")
                        .header("X-User-id", 10L)
                        .header("X-User-role", "OWNER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDishRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details:").value("Custom business validation failed"));
    }

    @Test
    void shouldHandleUnauthorizedExceptionFromHandler() throws Exception {

        when(dishUseCase.saveDish(any(Dish.class), anyLong()))
                .thenThrow(new UnauthorizedException("Unauthorized"));

        mockMvc.perform(post("/api/v1/dishes")
                        .header("X-User-id", 10L)
                        .header("X-User-role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDishRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error:").value("Unauthorized"));
    }


    /// /////////////////// Feature hu4 patch dish/////////////////////

    @Test
    void shouldUpdateDishSuccessfully() throws Exception {
        dish.setPrice(20000);
        dish.setDescription("New description");
        dish.setActive(true);

        when(dishUseCase.updateDish(anyLong(), any(Dish.class), anyLong())).thenReturn(dish);

        mockMvc.perform(patch("/api/v1/dishes/{dishId}", 1L)
                        .header("X-User-role", "OWNER")
                        .header("X-User-id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDishRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(20000))
                .andExpect(jsonPath("$.description").value("New description"))
                .andExpect(jsonPath("$.active").value(true));
    }


    @Test
    void shouldRejectWhenUserIsNotOwner() throws Exception {

        mockMvc.perform(patch("/api/v1/dishes/{dishId}", 1L)
                        .header("X-User-role", "EMPLOYEE")
                        .header("X-User-id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDishRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.details:").value("Only owners can update dishes"));
    }


    @Test
    void shouldHandleValidationExceptionFromHandler_patchOperation() throws Exception {

        when(dishUseCase.updateDish(anyLong(), any(Dish.class), anyLong()))
                .thenThrow(new ValidationException("Custom business validation failed"));

        mockMvc.perform(patch("/api/v1/dishes/{dishId}", 1L)
                        .header("X-User-role", "OWNER")
                        .header("X-User-id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDishRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details:").value("Custom business validation failed"));
    }

    ///////////////////////////////// FEATURE HU 10 //////////////////////////////////////////

    @Test
    void shouldReturnOkWhenRoleIsClient() throws Exception {
        PageResponse<GetAllDishesData> mockResponse = new PageResponse<>(
                List.of(new GetAllDishesData("Burger", "img.png", 10000,"fastFood")),
                1, 10, 1, 1, false, false, true, true
        );

        when(dishPageableService.getDishes("El Corral", null, 1, 10)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/dishes")
                        .header("X-User-role", "CLIENT")
                        .param("restaurantName", "El Corral")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Burger"));
    }


    @Test
    void shouldHandleUnauthorizedExceptionFromHandler_getAllDishes() throws Exception {

        when(dishUseCase.saveDish(any(Dish.class), anyLong()))
                .thenThrow(new UnauthorizedException("Unauthorized"));

        mockMvc.perform(get("/api/v1/dishes")
                        .header("X-User-role", "OWNER")
                        .param("restaurantName", "El Corral")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDishRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error:").value("Unauthorized"));
    }

    @Test
    void shouldReturnNotFoundWhenNoDishesAvailable() throws Exception {
        when(dishPageableService.getDishes("El Corral", null, 1, 10))
                .thenThrow(new DishNotFoundException("No dishes found for restaurant: El Corral"));

        mockMvc.perform(get("/api/v1/dishes")
                        .header("X-User-role", "CLIENT")
                        .param("restaurantName", "El Corral")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error:").value("Business validation error"))
                .andExpect(jsonPath("$.details:").value("No dishes found for restaurant: El Corral"));
    }

}

