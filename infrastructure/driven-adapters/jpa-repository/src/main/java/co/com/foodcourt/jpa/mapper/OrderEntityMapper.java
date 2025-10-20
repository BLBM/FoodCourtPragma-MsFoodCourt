package co.com.foodcourt.jpa.mapper;

import co.com.foodcourt.jpa.entity.OrderEntity;
import co.com.foodcourt.jpa.entity.OrderStatusEntity;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {DishEntityMapper.class,
        RestaurantEntityMapper.class,
        OrderDishEntityMapper.class},componentModel = "spring")
public interface OrderEntityMapper {


    @Mapping(target = "chef", ignore = true)
    @Mapping(target = "dishes", source = "orderDishes")
    Order toDomain(OrderEntity entity);

    @Mapping(target = "chefId", ignore = true)
    @Mapping(target = "orderDishes", source = "dishes")
    @Mapping(target = "restaurant", source = "restaurant")
    @Mapping(target = "clientId", source = "clientId")
    OrderEntity toEntity(Order domain);


    default OrderStatusEntity toEntityStatus(OrderStatus status) {
        return OrderStatusEntity.valueOf(status.name());
    }

    default List<OrderStatusEntity> toEntityStatusList(List<OrderStatus> statuses) {
        return statuses.stream()
                .map(this::toEntityStatus)
                .toList();
    }

    default OrderStatus toDomainStatus(OrderStatusEntity statusEntity) {
        return OrderStatus.valueOf(statusEntity.name());
    }
}
