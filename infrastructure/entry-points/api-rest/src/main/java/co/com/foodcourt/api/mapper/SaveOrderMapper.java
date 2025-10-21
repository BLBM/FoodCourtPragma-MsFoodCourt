package co.com.foodcourt.api.mapper;

import co.com.foodcourt.api.dto.CreateOrderRequest;
import co.com.foodcourt.api.dto.CreateOrderResponse;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderDish;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.model.restaurant.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SaveOrderMapper {

    SaveOrderMapper INSTANCE = Mappers.getMapper(SaveOrderMapper.class);

    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "chef", ignore = true)
    @Mapping(target = "securityPin", ignore = true)
    @Mapping(target = "deliveredDate", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "creationDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "restaurant", source = "restaurantId", qualifiedByName = "mapRestaurantFromId")
    @Mapping(target = "dishes", source = "dishes", qualifiedByName = "mapOrderDishes")
    Order toDomain(CreateOrderRequest request);

    @Mapping(target = "restaurantId", source = "restaurant.restaurantId")
    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    CreateOrderResponse toResponse(Order order);

    @Named("mapRestaurantFromId")
    default Restaurant mapRestaurantFromId(Long restaurantId) {
        if (restaurantId == null) return null;
        return Restaurant.builder()
                .restaurantId(restaurantId)
                .build();
    }


    @Named("mapOrderDishes")
    default List<OrderDish> mapOrderDishes(List<CreateOrderRequest.DishRequest> dishRequests) {
        if (dishRequests == null) return Collections.emptyList();
        return dishRequests.stream()
                .map(d -> OrderDish.builder()
                        .dish(Dish.builder().dishId(d.dishId()).build())
                        .quantity(d.quantity())
                        .build())
                .toList();
    }
}
