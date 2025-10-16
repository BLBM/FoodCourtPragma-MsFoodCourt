package co.com.foodcourt.api.controller;

import co.com.foodcourt.api.common.ErrorMessages;
import co.com.foodcourt.api.common.LogConstants;
import co.com.foodcourt.api.common.Rol;
import co.com.foodcourt.api.common.SwaggerConstants;
import co.com.foodcourt.api.dto.*;
import co.com.foodcourt.api.exception.UnauthorizedException;
import co.com.foodcourt.api.mapper.SaveDishMapper;
import co.com.foodcourt.api.mapper.UpdateDishMapper;
import co.com.foodcourt.api.service.DishPageableService;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.usecase.dish.DishUseCase;
import co.com.foodcourt.usecase.exception.ValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
@Tag(name = "Dishes", description = SwaggerConstants.TAG_DISHES_CONTROLLER)
public class DishController {

    private final DishUseCase dishUseCase;
    private final DishPageableService dishPageableService;

    @Operation(
            summary = SwaggerConstants.CREATE_DISH_SUMMARY,
            description = SwaggerConstants.CREATE_DISH_DESCRIPTION,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Dish data to create",
                    content = @Content(schema = @Schema(implementation = CreateDishRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Dish successfully created",
                            content = @Content(schema = @Schema(implementation = CreateDishResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body or missing fields",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - only OWNER can create dishes",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Unexpected internal error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @Parameters({
            @Parameter(name = "X-User-id", description = SwaggerConstants.USER_ROLE_DESCRIPTION, example = "1", required = true),
            @Parameter(name = "X-User-role", description = SwaggerConstants.USER_ROLE_DESCRIPTION, example = "OWNER", required = true)
    })
    @PostMapping
    public ResponseEntity<CreateDishResponse> createDish(@RequestHeader("X-User-id") Long ownerId,
                                                         @RequestHeader("X-User-role") String role,
                                                         @Valid @RequestBody CreateDishRequest createDishRequest){

        if(!Rol.OWNER.name().equalsIgnoreCase(role)){
            throw  new UnauthorizedException(ErrorMessages.INVALID_ROL_OWNER_DISHES.getMessage());
        }
        log.info(LogConstants.CREATE_DISH_REQUEST.getMessage(),createDishRequest.name());
        Dish disCreated = dishUseCase.saveDish(SaveDishMapper.INSTANCE.toDomain(createDishRequest),ownerId);
        log.info(LogConstants.CREATE_DISH_SUCCESS.getMessage(),createDishRequest.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(SaveDishMapper.INSTANCE.toResponse(disCreated));
    }

    @Operation(
            summary = SwaggerConstants.UPDATE_SUMMARY,
            description = SwaggerConstants.UPDATE_DESCRIPTION,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Partial dish data to update (only the provided fields will be modified)",
                    content = @Content(schema = @Schema(implementation = UpdateDishRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Dish successfully updated",
                            content = @Content(schema = @Schema(implementation = UpdateDishResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body or validation error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - only OWNER can update dishes",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Dish not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Unexpected internal error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @Parameters({
            @Parameter(name = "X-User-id", description = SwaggerConstants.USER_ROLE_DESCRIPTION, example = "1", required = true),
            @Parameter(name = "X-User-role", description = SwaggerConstants.USER_ROLE_DESCRIPTION, example = "OWNER", required = true)
    })
    @PatchMapping("/{dishId}")
    public ResponseEntity<UpdateDishResponse> updateDish(@PathVariable("dishId") Long dishId,
                                                         @RequestHeader("X-User-role") String role,
                                                         @RequestHeader("X-User-id") Long ownerId,
                                                         @RequestBody UpdateDishRequest partialDish){
        if(!Rol.OWNER.name().equalsIgnoreCase(role)){
            throw  new UnauthorizedException(ErrorMessages.INVALID_ROL_OWNER_UPDATE_DISHES.getMessage());
        }

        log.info(LogConstants.UPDATE_DISH_REQUEST.getMessage(),dishId);
        Dish disCreated = dishUseCase.updateDish(dishId,UpdateDishMapper.INSTANCE.toDomain(partialDish),ownerId);
        log.info(LogConstants.UPDATE_DISH_SUCCESS.getMessage(),dishId);
        return ResponseEntity.status(HttpStatus.OK).body(UpdateDishMapper.INSTANCE.toResponse(disCreated));
    }

    @GetMapping
    public ResponseEntity<PageResponse<GetAllDishesData>> listDishes(
            @RequestHeader(name = "X-User-role") String role,
            @RequestParam(name = "restaurantName") String restaurantName,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        if(!Rol.CLIENT.name().equalsIgnoreCase(role)){
            throw  new UnauthorizedException(ErrorMessages.INVALID_ROL_SHOW_RESTAURANTS.getMessage());
        }

        log.info(LogConstants.GET_ALL_DISHES_REQUEST.getMessage());
        PageResponse<GetAllDishesData> response =
                dishPageableService.getDishes(restaurantName, categoryId, page, size);
        log.info(LogConstants.GET_ALL_DISHES_SUCCESS.getMessage());
        return ResponseEntity.ok(response);
    }

}
