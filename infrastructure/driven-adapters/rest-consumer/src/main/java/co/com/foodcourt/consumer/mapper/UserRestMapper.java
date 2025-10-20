package co.com.foodcourt.consumer.mapper;

import co.com.foodcourt.consumer.dto.UserResponse;
import co.com.foodcourt.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserRestMapper {

    UserRestMapper INSTANCE = Mappers.getMapper(UserRestMapper.class);

    @Mapping(target = "userId" ,ignore = true)
    User toDomain(UserResponse userResponse);
}
