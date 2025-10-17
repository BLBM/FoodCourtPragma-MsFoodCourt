package co.com.foodcourt.usecase.util;

import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.user.User;
import co.com.foodcourt.usecase.common.ValidationMessages;
import co.com.foodcourt.usecase.exception.ValidationException;

public class ValidateUser {

    private static final String ROL_OWNER = "OWNER";
    private static final String ROL_EMPLOYEE = "EMPLOYEE";

    private static void validateOwner(String role) {
        if (!ROL_OWNER.equals(role)) {
            throw new ValidationException(ValidationMessages.INVALID_ROL_OWNER_RESTAURANT.getMessage());
        }
    }

    public static void validateUser(User user) {
        if (user == null) {
            throw new ValidationException(ValidationMessages.INVALID_RESTAURANT.getMessage());
        }
        validateOwner(user.getRole());
    }

    public static void validateOwner(Long ownerId, Long restaurantOwner){
        if (!restaurantOwner.equals(ownerId)){
            throw new ValidationException(ValidationMessages.INVALID_OWNER_OF_RESTAURANT.getMessage());
        }
    }

    public static void validateRoleEmployee(String role) {
        if (!ROL_EMPLOYEE.equals(role)) {
            throw new ValidationException(ValidationMessages.INVALID_EMPLOYEE.getMessage());
        }
    }
}
