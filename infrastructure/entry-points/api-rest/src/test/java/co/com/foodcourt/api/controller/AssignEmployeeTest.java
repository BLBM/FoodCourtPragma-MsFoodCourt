package co.com.foodcourt.api.controller;


import co.com.foodcourt.api.dto.AssignEmployeeRequest;
import co.com.foodcourt.api.global_exception_handler.GlobalExceptionHandler;
import co.com.foodcourt.api.service.RestaurantPageableService;
import co.com.foodcourt.usecase.createrestaurant.CreateRestaurantUseCase;
import co.com.foodcourt.usecase.employee_restaurant.EmployeeRestaurantUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {RestaurantController.class, GlobalExceptionHandler.class})
class AssignEmployeeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeRestaurantUseCase employeeRestaurantUseCase;

    @MockitoBean
    private CreateRestaurantUseCase createRestaurantUseCase;

    @MockitoBean
    private RestaurantPageableService restaurantPageableService;

    private static final String BASE_URL = "/api/v1/restaurants";
    private static final Long RESTAURANT_ID = 100L;
    private static final Long OWNER_ID = 50L;
    private static final Long EMPLOYEE_ID = 1L;
    private static final String OWNER_ROLE = "OWNER";
    private static final String EMPLOYEE_ROLE = "EMPLOYEE";

    @Test
    void assignEmployeeToRestaurant_shouldAssignSuccessfully() throws Exception {

        AssignEmployeeRequest request = new AssignEmployeeRequest(EMPLOYEE_ID);

        doNothing().when(employeeRestaurantUseCase)
                .assign(EMPLOYEE_ID, RESTAURANT_ID, OWNER_ID);

        mockMvc.perform(post(BASE_URL + "/{restaurantId}/employees", RESTAURANT_ID)
                        .header("X-User-id", OWNER_ID)
                        .header("X-User-role", OWNER_ROLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists());

        verify(employeeRestaurantUseCase).assign(EMPLOYEE_ID, RESTAURANT_ID, OWNER_ID);
    }

    @Test
    void assignEmployeeToRestaurant_shouldThrowUnauthorizedWhenRoleIsNotOwner() throws Exception {
        AssignEmployeeRequest request = new AssignEmployeeRequest(EMPLOYEE_ID);

        mockMvc.perform(post(BASE_URL + "/{restaurantId}/employees", RESTAURANT_ID)
                        .header("X-User-id", OWNER_ID)
                        .header("X-User-role", EMPLOYEE_ROLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error:").value("Unauthorized"));

        verify(employeeRestaurantUseCase, never()).assign(any(), any(), any());
    }

    @Test
    void assignEmployeeToRestaurant_shouldReturnBadRequestWhenRequestBodyIsInvalid() throws Exception {
        mockMvc.perform(post(BASE_URL + "/{restaurantId}/employees", RESTAURANT_ID)
                        .header("X-User-id", OWNER_ID)
                        .header("X-User-role", OWNER_ROLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(employeeRestaurantUseCase, never()).assign(any(), any(), any());
    }

}
