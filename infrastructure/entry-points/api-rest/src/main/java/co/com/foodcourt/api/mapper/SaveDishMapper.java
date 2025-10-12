package co.com.foodcourt.api.mapper;

import co.com.foodcourt.api.dto.CreateDishRequest;
import co.com.foodcourt.api.dto.CreateDishResponse;
import co.com.foodcourt.model.category.Category;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.model.restaurant.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SaveDishMapper {

    SaveDishMapper INSTANCE = Mappers.getMapper(SaveDishMapper.class);

    @Mapping(target = "dishId" , ignore = true)
    @Mapping(target = "active" , ignore = true)
    @Mapping(target = "restaurant", expression = "java(createRestaurantWithId(createDishRequest.restaurantId()))")
    @Mapping(target = "category", expression = "java(createCategoryWithId(createDishRequest.categoryId()))")
    Dish toDomain(CreateDishRequest createDishRequest);

    CreateDishResponse toResponse(Dish dish);

    default Restaurant createRestaurantWithId(Long restaurantId) {
        return Restaurant.builder()
                .restaurantId(restaurantId)
                .build();
    }

    default Category createCategoryWithId(Long categoryId) {
        return Category.builder()
                .categoryId(categoryId)
                .build();
    }

}
