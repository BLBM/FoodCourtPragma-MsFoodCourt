package co.com.foodcourt.api.controller;

import co.com.foodcourt.api.dto.CreateDishRequest;
import co.com.foodcourt.api.dto.CreateDishResponse;
import co.com.foodcourt.api.dto.UpdateDishRequest;
import co.com.foodcourt.api.exception.UnauthorizedException;
import co.com.foodcourt.api.global_exception_handler.GlobalExceptionHandler;
import co.com.foodcourt.model.plate.Dish;
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

        CreateDishResponse createDishResponse = new CreateDishResponse(
                "Cheeseburger",
                25000,
              "Delicious burger");

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




}

