package co.com.foodcourt.api.controller;
import co.com.foodcourt.api.common.ErrorMessages;
import co.com.foodcourt.api.common.LogConstants;
import co.com.foodcourt.api.common.Rol;
import co.com.foodcourt.api.common.SwaggerConstants;
import co.com.foodcourt.api.dto.CreateRestaurantRequest;
import co.com.foodcourt.api.dto.CreateRestaurantResponse;
import co.com.foodcourt.api.dto.ErrorResponse;
import co.com.foodcourt.api.exception.UnauthorizedException;
import co.com.foodcourt.api.mapper.SaveRestaurantMapper;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.usecase.createrestaurant.CreateRestaurantUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping(value = "/api/v1/restaurants", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Tag(name = "Restaurants", description = SwaggerConstants.TAG_RESTAURANT_CONTROLLER)
public class RestaurantController {

    private final CreateRestaurantUseCase createRestaurantUseCase;

    @Operation(
            summary = SwaggerConstants.CREATE_RESTAURANT_SUMMARY,
            description = SwaggerConstants.CREATE_RESTAURANT_DESCRIPTION,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Restaurant data to create",
                    content = @Content(schema = @Schema(implementation = CreateRestaurantRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Restaurant successfully created",
                            content = @Content(schema = @Schema(implementation = CreateRestaurantResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body or missing required fields",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - only ADMIN can create restaurants",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict - restaurant already exists or invalid owner ID",
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
    public ResponseEntity<CreateRestaurantResponse> createRestaurant(@RequestHeader("X-User-id") Long ownerId,
                                                                     @RequestHeader("X-User-role") String role,
                                                                     @Valid @RequestBody CreateRestaurantRequest restaurantRequest) {

        if(!Rol.ADMIN.name().equalsIgnoreCase(role)){
            throw  new UnauthorizedException(ErrorMessages.INVALID_ROL_OWNER_DISHES.getMessage());
        }
        log.info(LogConstants.CREATE_RESTAURANT_REQUEST.getMessage(),restaurantRequest.name());
        Restaurant restaurantCreated = createRestaurantUseCase.saveRestaurant(SaveRestaurantMapper.INSTANCE.toDomain(restaurantRequest));
        log.info(LogConstants.CREATE_RESTAURANT_SUCCESS.getMessage(),restaurantRequest.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(SaveRestaurantMapper.INSTANCE.toDto(restaurantCreated));

    }

}
