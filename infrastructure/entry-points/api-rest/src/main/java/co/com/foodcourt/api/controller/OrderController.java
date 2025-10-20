package co.com.foodcourt.api.controller;

import co.com.foodcourt.api.common.ErrorMessages;
import co.com.foodcourt.api.common.LogConstants;
import co.com.foodcourt.api.common.Rol;
import co.com.foodcourt.api.common.SwaggerConstants;
import co.com.foodcourt.api.dto.*;
import co.com.foodcourt.api.exception.UnauthorizedException;
import co.com.foodcourt.api.mapper.SaveOrderMapper;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.usecase.order.OrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@Slf4j
@RestController
@RequestMapping(value = "/api/v1/orders", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Orders", description = SwaggerConstants.TAG_ORDERS_CONTROLLER)
public class OrderController {

    private final OrderUseCase orderUseCase;


    @Operation(
            summary = SwaggerConstants.CREATE_ORDER_SUMMARY,
            description = SwaggerConstants.CREATE_ORDER_DESCRIPTION,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "create a new order",
                    content = @Content(schema = @Schema(implementation = CreateRestaurantRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "order create successfully",
                            content = @Content(schema = @Schema(implementation = CreateRestaurantResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "bad request",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - only client can to create new orders",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Unexpected internal error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(
            @RequestHeader("X-User-role") String role,
            @RequestHeader("X-User-id") Long clientId,
            @Valid @RequestBody CreateOrderRequest request){

        if (!Rol.CLIENT.name().equalsIgnoreCase(role)) {
            throw new UnauthorizedException(ErrorMessages.INVALID_ROLE_CREATE_ORDER.getMessage());
        }

        log.info(LogConstants.CREATE_ORDER_SUCCESS.getMessage(), clientId, request.restaurantId());

        Order order = orderUseCase.createOrder(SaveOrderMapper.INSTANCE.toDomain(request),clientId);

        CreateOrderResponse response = SaveOrderMapper.INSTANCE.toResponse(order);

        log.info(LogConstants.CREATE_ORDER_REQUEST.getMessage(), response.orderId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
