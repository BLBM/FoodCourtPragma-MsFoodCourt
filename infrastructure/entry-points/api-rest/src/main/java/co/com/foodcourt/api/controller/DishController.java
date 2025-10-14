package co.com.foodcourt.api.controller;

import co.com.foodcourt.api.common.ErrorMessages;
import co.com.foodcourt.api.common.LogConstants;
import co.com.foodcourt.api.common.rol;
import co.com.foodcourt.api.dto.CreateDishRequest;
import co.com.foodcourt.api.dto.CreateDishResponse;
import co.com.foodcourt.api.dto.UpdateDishRequest;
import co.com.foodcourt.api.dto.UpdateDishResponse;
import co.com.foodcourt.api.exception.UnauthorizedException;
import co.com.foodcourt.api.mapper.SaveDishMapper;
import co.com.foodcourt.api.mapper.UpdateDishMapper;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.usecase.dish.DishUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishUseCase dishUseCase;

    @PostMapping
    public ResponseEntity<CreateDishResponse> createDish(@RequestHeader("X-User-id") Long ownerId,
                                                         @RequestHeader("X-User-role") String role,
                                                         @Valid @RequestBody CreateDishRequest createDishRequest){

        if(!rol.OWNER.name().equalsIgnoreCase(role)){
            throw  new UnauthorizedException(ErrorMessages.INVALID_ROL_OWNER_DISHES.getMessage());
        }
        log.info(LogConstants.CREATE_DISH_REQUEST.getMessage(),createDishRequest.name());
        Dish disCreated = dishUseCase.saveDish(SaveDishMapper.INSTANCE.toDomain(createDishRequest),ownerId);
        log.info(LogConstants.CREATE_DISH_SUCCESS.getMessage(),createDishRequest.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(SaveDishMapper.INSTANCE.toResponse(disCreated));
    }

    @PatchMapping("/{dishId}")
    public ResponseEntity<UpdateDishResponse> updateDish(@PathVariable("dishId") Long dishId,
                                                         @RequestHeader("X-User-role") String role,
                                                         @RequestHeader("X-User-id") Long ownerId,
                                                         @RequestBody UpdateDishRequest partialDish){
        if(!rol.OWNER.name().equalsIgnoreCase(role)){
            throw  new UnauthorizedException(ErrorMessages.INVALID_ROL_OWNER_UPDATE_DISHES.getMessage());
        }

        log.info(LogConstants.UPDATE_DISH_REQUEST.getMessage(),dishId);
        Dish disCreated = dishUseCase.updateDish(dishId,UpdateDishMapper.INSTANCE.toDomain(partialDish),ownerId);
        log.info(LogConstants.UPDATE_DISH_SUCCESS.getMessage(),dishId);
        return ResponseEntity.status(HttpStatus.OK).body(UpdateDishMapper.INSTANCE.toResponse(disCreated));
    }

}
