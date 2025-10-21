package co.com.foodcourt.api.mapper;

import co.com.foodcourt.api.dto.UpdateOrderResponse;
import co.com.foodcourt.api.dto.UpdateOrderStatusRequest;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderStatus;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UpdateOrderMapper {

    UpdateOrderMapper INSTANCE = Mappers.getMapper(UpdateOrderMapper.class);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "status", expression = "java(mapStatus(request.newStatus()))")
    @Mapping(target = "securityPin", source = "pin")
    Order toDomain(UpdateOrderStatusRequest request);


    UpdateOrderResponse toResponse(Order updatedOrder);


    default OrderStatus mapStatus(String status) {
        return status != null ? OrderStatus.valueOf(status.toUpperCase()) : null;
    }
}
