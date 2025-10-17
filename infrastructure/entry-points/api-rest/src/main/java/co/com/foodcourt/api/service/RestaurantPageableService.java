package co.com.foodcourt.api.service;

import co.com.foodcourt.api.dto.GetAllRestaurantsData;
import co.com.foodcourt.api.dto.PageResponse;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.usecase.createrestaurant.CreateRestaurantUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantPageableService {

    private final CreateRestaurantUseCase createRestaurantUseCase;
    private final PageableService pageableService;

    public PageResponse<GetAllRestaurantsData> getRestaurants(int page, int size) {
        List<Restaurant> all = createRestaurantUseCase.getAllRestaurants();

        return pageableService.paginate(
                all,
                page,
                size,
                r -> new GetAllRestaurantsData(r.getName(), r.getUrlLogo())
        );
    }

}
