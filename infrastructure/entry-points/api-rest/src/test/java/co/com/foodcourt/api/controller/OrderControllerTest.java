package co.com.foodcourt.api.controller;


import co.com.foodcourt.api.common.ErrorMessages;
import co.com.foodcourt.api.common.Rol;
import co.com.foodcourt.api.dto.CreateOrderRequest;
import co.com.foodcourt.api.dto.GetAllOrdersData;
import co.com.foodcourt.api.dto.OrderDishData;
import co.com.foodcourt.api.dto.PageResponse;
import co.com.foodcourt.api.global_exception_handler.GlobalExceptionHandler;
import co.com.foodcourt.api.service.OrderPageableService;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderDish;
import co.com.foodcourt.model.order.OrderStatus;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.restaurant.exception.RestaurantNotFoundException;
import co.com.foodcourt.usecase.order.OrderUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {OrderController.class, GlobalExceptionHandler.class})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderUseCase orderUseCase;

    @MockitoBean
    private OrderPageableService orderPageableService;

    private static final String BASE_URL = "/api/v1/orders";
    private static final Long CLIENT_ID = 500L;
    private static final Long EMPLOYEE_ID = 200L;
    private static final String CLIENT_ROLE = "CLIENT";
    private static final String OWNER_ROLE = "OWNER";
    private PageResponse<GetAllOrdersData> mockResponse;

    @BeforeEach
    void setUp() {
        OrderDishData dish1 = new OrderDishData(10L, "Burger Classic", 2);
        OrderDishData dish2 = new OrderDishData(12L, "French Fries", 1);

        GetAllOrdersData orderData = new GetAllOrdersData(
                1L,
                500L,
                LocalDateTime.of(2025, 10, 20, 11, 30, 0),
                OrderStatus.PENDING,
                "PipoBurger",
                List.of(dish1, dish2)
        );

        mockResponse = new PageResponse<>(
                List.of(orderData),
                1,
                3,
                1,
                1,
                false,
                false,
                true,
                true
        );
    }

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                1L,
                List.of(new CreateOrderRequest.DishRequest(10L, 2))
        );

        Order mockOrder = Order.builder()
                .orderId(99L)
                .restaurant(Restaurant.builder().restaurantId(1L).build())
                .clientId(CLIENT_ID)
                .status(OrderStatus.PENDING)
                .creationDate(LocalDateTime.now())
                .dishes(List.of(OrderDish.builder()
                        .dish(Dish.builder().dishId(10L).build())
                        .quantity(2)
                        .build()))
                .build();

        when(orderUseCase.createOrder(any(Order.class), eq(CLIENT_ID)))
                .thenReturn(mockOrder);

        mockMvc.perform(post(BASE_URL)
                        .header("X-User-id", CLIENT_ID)
                        .header("X-User-role", CLIENT_ROLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(99))
                .andExpect(jsonPath("$.restaurantId").value(1))
                .andExpect(jsonPath("$.clientId").value(500))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.creationDate").exists());

        verify(orderUseCase).createOrder(any(Order.class), eq(CLIENT_ID));
    }

    @Test
    void shouldThrowUnauthorizedWhenRoleIsNotClient() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                1L,
                List.of(new CreateOrderRequest.DishRequest(10L, 2))
        );

        mockMvc.perform(post(BASE_URL)
                        .header("X-User-id", CLIENT_ID)
                        .header("X-User-role", OWNER_ROLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error:").value("Unauthorized"));

        verify(orderUseCase, never()).createOrder(any(), anyLong());
    }

    @Test
    void shouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        String invalidJson = """
                {
                    "restaurantId": null,
                    "dishes": []
                }
                """;

        mockMvc.perform(post(BASE_URL)
                        .header("X-User-id", CLIENT_ID)
                        .header("X-User-role", CLIENT_ROLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(orderUseCase, never()).createOrder(any(), anyLong());
    }


    @Test
    void shouldReturnNotFoundWhenNoDishesAvailable() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                1L,
                List.of(new CreateOrderRequest.DishRequest(10L, 2))
        );

        when(orderUseCase.createOrder(any(Order.class), eq(CLIENT_ID)))
                .thenThrow(new RestaurantNotFoundException("Not found Restaurant"));

        mockMvc.perform(post(BASE_URL)
                        .header("X-User-id", CLIENT_ID)
                        .header("X-User-role", CLIENT_ROLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error:").value("Business validation error"))
                .andExpect(jsonPath("$.details:").value("Not found Restaurant"));
    }

    /* Feature hu 12*/

    @Test
    void shouldListOrdersSuccessfully_WhenValidHeadersAndParams() throws Exception {
        when(orderPageableService.getOrders(EMPLOYEE_ID, "PENDING", 1, 3))
                .thenReturn(mockResponse);

        mockMvc.perform(get(BASE_URL)
                        .header("X-User-role", Rol.EMPLOYEE.name())
                        .header("X-User-id", EMPLOYEE_ID)
                        .param("status", "PENDING")
                        .param("page", "1")
                        .param("size", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderId").value(1))
                .andExpect(jsonPath("$.content[0].clientId").value(500))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"))
                .andExpect(jsonPath("$.content[0].restaurantName").value("PipoBurger"))
                .andExpect(jsonPath("$.content[0].dishes[0].dishId").value(10))
                .andExpect(jsonPath("$.content[0].dishes[0].dishName").value("Burger Classic"))
                .andExpect(jsonPath("$.content[0].dishes[0].quantity").value(2))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(3))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));

        verify(orderPageableService, times(1))
                .getOrders(EMPLOYEE_ID, "PENDING", 1, 3);
    }

    @Test
    void shouldThrowUnauthorized_WhenRoleIsNotEmployee() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("X-User-role", Rol.CLIENT.name())
                        .header("X-User-id", EMPLOYEE_ID)
                        .param("status", "PENDING")
                        .param("page", "1")
                        .param("size", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error:").value("Unauthorized"))
                .andExpect(jsonPath("$.details:").value(ErrorMessages.INVALID_ROL_SHOW_ORDERS.getMessage()));

        verify(orderPageableService, never())
                .getOrders(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void shouldReturnBadRequest_WhenMissingUserRoleHeader() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("X-User-id", EMPLOYEE_ID)
                        .param("status", "PENDING")
                        .param("page", "1")
                        .param("size", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error:").value(ErrorMessages.BUSINESS_VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("$.details:")
                        .value(ErrorMessages.INVALID_REQUEST_HEADER.getMessage() + "X-User-role"));

        verify(orderPageableService, never())
                .getOrders(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void shouldReturnBadRequest_WhenMissingUserIdHeader() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("X-User-role", Rol.EMPLOYEE.name())
                        .param("status", "PENDING")
                        .param("page", "1")
                        .param("size", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error:").value(ErrorMessages.BUSINESS_VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("$.details:")
                        .value(ErrorMessages.INVALID_REQUEST_HEADER.getMessage() + "X-User-id"));

        verify(orderPageableService, never())
                .getOrders(anyLong(), anyString(), anyInt(), anyInt());
    }
}

