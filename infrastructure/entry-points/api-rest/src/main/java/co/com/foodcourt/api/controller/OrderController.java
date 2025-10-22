package co.com.foodcourt.api.controller;

import co.com.foodcourt.api.common.ErrorMessages;
import co.com.foodcourt.api.common.LogConstants;
import co.com.foodcourt.api.common.Rol;
import co.com.foodcourt.api.common.SwaggerConstants;
import co.com.foodcourt.api.dto.*;
import co.com.foodcourt.api.exception.UnauthorizedException;
import co.com.foodcourt.api.mapper.SaveOrderMapper;
import co.com.foodcourt.api.mapper.UpdateOrderMapper;
import co.com.foodcourt.api.service.OrderPageableService;
import co.com.foodcourt.model.actor.Actor;
import co.com.foodcourt.model.actor.ActorRole;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.usecase.create_order.CreateOrderUseCase;
import co.com.foodcourt.usecase.update_order.UpdateOrderUseCase;
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

    private final CreateOrderUseCase createOrderUseCase;
    private final OrderPageableService orderPageableService;
    private final UpdateOrderUseCase updateOrderUseCase;


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
            @RequestHeader(name = "X-User-Id") Long userId,
            @RequestHeader(name = "X-User-Email") String email,
            @RequestHeader(name = "X-User-Role") String role,
            @Valid @RequestBody CreateOrderRequest request){

        if (!Rol.CLIENT.name().equalsIgnoreCase(role)) {
            throw new UnauthorizedException(ErrorMessages.INVALID_ROLE_CREATE_ORDER.getMessage());
        }

        Actor actor = Actor.builder()
                .id(userId)
                .email(email)
                .role(ActorRole.valueOf(role.toUpperCase()))
                .build();

        log.info(LogConstants.CREATE_ORDER_SUCCESS.getMessage(), userId, request.restaurantId());

        Order order = createOrderUseCase.createOrder(SaveOrderMapper.INSTANCE.toDomain(request),actor);

        CreateOrderResponse response = SaveOrderMapper.INSTANCE.toResponse(order);

        log.info(LogConstants.CREATE_ORDER_REQUEST.getMessage(), response.orderId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    public ResponseEntity<PageResponse<GetAllOrdersData>> listOrdersByStatus(
            @RequestHeader(name = "X-User-role") String role,
            @RequestHeader(name = "X-User-id") Long employeeId,
            @RequestParam(name = "status") String status,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "3") int size) {

        if (!Rol.EMPLOYEE.name().equalsIgnoreCase(role)) {
            throw new UnauthorizedException(ErrorMessages.INVALID_ROL_SHOW_ORDERS.getMessage());
        }

        log.info(LogConstants.GET_ALL_ORDERS_REQUEST.getMessage());
        return ResponseEntity.ok(orderPageableService.getOrders(employeeId, status, page, size));
    }


    @PatchMapping("/{orderId}/status")
    public ResponseEntity<UpdateOrderResponse> updateOrderStatus(
            @PathVariable(name = "orderId") Long orderId,
            @RequestHeader(name = "X-User-Id") Long userId,
            @RequestHeader(name = "X-User-Email") String email,
            @RequestHeader(name = "X-User-Role") String role,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        log.info("Updating status for order [{}] to [{}]", orderId, request.newStatus());

        Actor actor = Actor.builder()
                .id(userId)
                .email(email)
                .role(ActorRole.valueOf(role.toUpperCase()))
                .build();

        Order orderUpdate = UpdateOrderMapper.INSTANCE.toDomain(request);

        Order updated = updateOrderUseCase.updateStatus(
                orderId,
                orderUpdate.getStatus(),
                actor,
                orderUpdate.getSecurityPin()
        );

        return ResponseEntity.ok(UpdateOrderMapper.INSTANCE.toResponse(updated));
    }


}
