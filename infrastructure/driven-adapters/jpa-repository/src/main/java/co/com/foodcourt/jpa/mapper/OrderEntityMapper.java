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

@Mapper(uses = {DishEntityMapper.class, RestaurantEntityMapper.class})
public interface OrderEntityMapper {

    Order toDomain(OrderEntity entity);

    OrderEntity toEntity(Order domain);

    List<Order> toDomainList(List<OrderEntity> entities);

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
