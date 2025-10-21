package co.com.foodcourt.jpa.mapper;

import co.com.foodcourt.jpa.entity.OrderDishEntity;
import co.com.foodcourt.model.order.OrderDish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = DishEntityMapper.class, componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderDishEntityMapper {

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "id", ignore = true)
    OrderDishEntity toEntity(OrderDish domain);

    @Mapping(target = "dish", source = "dish")
    OrderDish toDomain(OrderDishEntity entity);
}