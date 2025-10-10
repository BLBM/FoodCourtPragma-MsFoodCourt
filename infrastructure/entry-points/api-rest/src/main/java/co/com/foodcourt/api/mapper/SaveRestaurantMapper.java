package co.com.foodcourt.api.mapper;

import co.com.foodcourt.api.dto.CreateRestaurantRequest;
import co.com.foodcourt.api.dto.CreateRestaurantResponse;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SaveRestaurantMapper {

    SaveRestaurantMapper INSTANCE = Mappers.getMapper(SaveRestaurantMapper.class);

    @Mapping(target = "restaurantId", ignore = true)
    @Mapping(target = "owner", expression = "java(createUserWithId(saveRestaurantRequest.ownerId()))")
    Restaurant toDomain(CreateRestaurantRequest saveRestaurantRequest);

    @Mapping(target = "owner", expression = "java(getOwnerFullName(restaurant))")
    CreateRestaurantResponse toDto (Restaurant restaurant);

    default User createUserWithId(Long ownerId) {
        return User.builder()
                .userId(ownerId)
                .build();
    }

    default String getOwnerFullName(Restaurant restaurant) {
        if (restaurant.getOwner() != null &&
                restaurant.getOwner().getFirstName() != null &&
                restaurant.getOwner().getLastName() != null) {
            return restaurant.getOwner().getFirstName() + " " +
                    restaurant.getOwner().getLastName();
        }
        return "Unknown";
    }

}

