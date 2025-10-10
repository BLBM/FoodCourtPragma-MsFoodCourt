package co.com.foodcourt.api.controller;
import co.com.foodcourt.api.common.LogConstants;
import co.com.foodcourt.api.dto.CreateRestaurantRequest;
import co.com.foodcourt.api.dto.CreateRestaurantResponse;
import co.com.foodcourt.api.mapper.SaveRestaurantMapper;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.usecase.createrestaurant.CreateRestaurantUseCase;
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
public class RestaurantController {

    private final CreateRestaurantUseCase createRestaurantUseCase;

    @PostMapping
    public ResponseEntity<CreateRestaurantResponse> createRestaurant(@Valid @RequestBody CreateRestaurantRequest restaurantRequest) {
        log.info(LogConstants.CREATE_RESTAURANT_REQUEST.getMessage(),restaurantRequest.name());
        Restaurant restaurantCreated = createRestaurantUseCase.saveRestaurant(SaveRestaurantMapper.INSTANCE.toDomain(restaurantRequest));
        log.info(LogConstants.CREATE_RESTAURANT_SUCCESS.getMessage(),restaurantRequest.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(SaveRestaurantMapper.INSTANCE.toDto(restaurantCreated));

    }

}
