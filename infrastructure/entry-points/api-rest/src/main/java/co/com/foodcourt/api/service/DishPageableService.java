package co.com.foodcourt.api.service;

import co.com.foodcourt.api.dto.GetAllDishesData;
import co.com.foodcourt.api.dto.PageResponse;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.usecase.dish.DishUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DishPageableService {

    private final DishUseCase dishUseCase;
    private final PageableService pageableService;

    public PageResponse<GetAllDishesData> getDishes(String restaurantName, Long categoryId, int page, int size) {
        List<Dish> dishes = dishUseCase.getAllDishesByRestaurant(restaurantName, categoryId);

        return pageableService.paginate(
                dishes,
                page,
                size,
                d -> new GetAllDishesData(
                        d.getName(),
                        d.getDescription(),
                        d.getPrice(),
                        d.getCategory().getName()
                )
        );
    }
}
