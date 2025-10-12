package co.com.foodcourt.usecase.util;

import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.usecase.common.ValidationMessages;
import co.com.foodcourt.usecase.exception.ValidationException;

public class ValidateDish {

    private static final int MIN_VALUE = 0;


    private static void validatePrice(Integer dishPrice) {
        if (dishPrice <= MIN_VALUE) {
            throw new ValidationException(ValidationMessages.INVALID_DISH_PRICE.getMessage());
        }
    }

    public static void validateDish(Dish dish){
        if (dish == null) {
            throw new ValidationException(ValidationMessages.INVALID_DISH.getMessage());
        }
        validatePrice(dish.getPrice());
    }

}
