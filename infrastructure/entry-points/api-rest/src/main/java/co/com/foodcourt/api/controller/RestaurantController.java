package co.com.foodcourt.api.controller;
import co.com.foodcourt.api.common.ErrorMessages;
import co.com.foodcourt.api.common.LogConstants;
import co.com.foodcourt.api.common.Rol;
import co.com.foodcourt.api.common.SwaggerConstants;
import co.com.foodcourt.api.dto.*;
import co.com.foodcourt.api.exception.UnauthorizedException;
import co.com.foodcourt.api.mapper.SaveRestaurantMapper;
import co.com.foodcourt.api.service.RestaurantPageableService;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.usecase.createrestaurant.CreateRestaurantUseCase;
import co.com.foodcourt.usecase.employee_restaurant.EmployeeRestaurantUseCase;
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
    private final RestaurantPageableService restaurantPageableService;
    private final EmployeeRestaurantUseCase employeeRestaurantUseCase;


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
    public ResponseEntity<CreateRestaurantResponse> createRestaurant(@RequestHeader("X-User-role") String role,
                                                                     @Valid @RequestBody CreateRestaurantRequest restaurantRequest) {

        if(!Rol.ADMIN.name().equalsIgnoreCase(role)){
            throw  new UnauthorizedException(ErrorMessages.INVALID_RESTAURANT_ROL.getMessage());
        }
        log.info(LogConstants.CREATE_RESTAURANT_REQUEST.getMessage(),restaurantRequest.name());
        Restaurant restaurantCreated = createRestaurantUseCase.saveRestaurant(SaveRestaurantMapper.INSTANCE.toDomain(restaurantRequest));
        log.info(LogConstants.CREATE_RESTAURANT_SUCCESS.getMessage(),restaurantRequest.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(SaveRestaurantMapper.INSTANCE.toDto(restaurantCreated));

    }

    @GetMapping
    public ResponseEntity<PageResponse<GetAllRestaurantsData>> listRestaurants(
            @RequestHeader(name = "X-User-role") String role,
            @RequestParam(name = "page",defaultValue = "1") int page,
            @RequestParam(name = "size",defaultValue = "3") int size) {

        if(!Rol.CLIENT.name().equalsIgnoreCase(role)){
            throw  new UnauthorizedException(ErrorMessages.INVALID_ROL_SHOW_RESTAURANTS.getMessage());
        }
        log.info(LogConstants.GET_ALL_RESTAURANT_REQUEST.getMessage());
        return ResponseEntity.ok(restaurantPageableService.getRestaurants(page, size));
    }

    @PostMapping("/{restaurantId}/employees")
    public ResponseEntity<MessageResponse> assignEmployeeToRestaurant(
            @RequestHeader(name = "X-User-id") Long ownerId,
            @RequestHeader(name = "X-User-role") String role,
            @PathVariable(name = "restaurantId") Long restaurantId,
            @Valid @RequestBody AssignEmployeeRequest request
    ){
        if (!Rol.OWNER.name().equalsIgnoreCase(role)) {
            throw new UnauthorizedException(ErrorMessages.INVALID_ROL_ASSIGN_EMPLOYEE.getMessage());
        }

        log.info(LogConstants.ASSIGN_EMPLOYEE_REQUEST.getMessage(),request.employeeId());
        employeeRestaurantUseCase.assign(request.employeeId(),restaurantId,ownerId);
        log.info(LogConstants.ASSIGN_EMPLOYEE_SUCCESS.getMessage(),request.employeeId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse(LogConstants.ASSIGN_EMPLOYEE_SUCCESS.getMessage()));
    }



}
