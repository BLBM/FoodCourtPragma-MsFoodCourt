package co.com.foodcourt.jpa.mapper;

import co.com.foodcourt.jpa.entity.OrderDishEntity;
import co.com.foodcourt.jpa.entity.OrderEntity;
import co.com.foodcourt.jpa.entity.OrderStatusEntity;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderDish;
import co.com.foodcourt.model.order.OrderStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {DishEntityMapper.class, RestaurantEntityMapper.class},componentModel = "spring")
public interface OrderEntityMapper {


    @Mapping(target = "chef", ignore = true)
    @Mapping(target = "dishes", source = "orderDishes")
    Order toDomain(OrderEntity entity);

    @Mapping(target = "chefId", source = "chef.userId")
    @Mapping(target = "orderDishes", source = "dishes")
    OrderEntity toEntity(Order domain);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "id", ignore = true)
    OrderDishEntity toEntity(OrderDish orderDish);




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
