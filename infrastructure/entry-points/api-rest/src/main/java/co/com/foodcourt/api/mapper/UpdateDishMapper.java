package co.com.foodcourt.api.mapper;

import co.com.foodcourt.api.dto.UpdateDishRequest;
import co.com.foodcourt.api.dto.UpdateDishResponse;
import co.com.foodcourt.model.plate.Dish;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UpdateDishMapper {

    UpdateDishMapper INSTANCE = Mappers.getMapper(UpdateDishMapper.class);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    Dish toDomain(UpdateDishRequest updateDishRequest);

    UpdateDishResponse toResponse(Dish dishUpdated);
}
