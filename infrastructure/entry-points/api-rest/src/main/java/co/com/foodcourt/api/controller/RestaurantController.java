package co.com.foodcourt.api.controller;
import co.com.foodcourt.api.dto.CreateRestaurantRequest;
import co.com.foodcourt.api.dto.CreateRestaurantResponse;
import co.com.foodcourt.api.mapper.SaveRestaurantMapper;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.usecase.createrestaurant.CreateRestaurantUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class RestaurantController {

    private final CreateRestaurantUseCase createRestaurantUseCase;

    @PostMapping(path = "/createRestaurant")
    public ResponseEntity<CreateRestaurantResponse> createRestaurant(@RequestBody CreateRestaurantRequest restaurantRequest) {

        Restaurant restaurantCreated = createRestaurantUseCase.saveRestaurant(SaveRestaurantMapper.INSTANCE.toDomain(restaurantRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(SaveRestaurantMapper.INSTANCE.toDto(restaurantCreated));

    }

}
