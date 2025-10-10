package co.com.foodcourt.usecase.util;

import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.usecase.common.ValidationMessages;
import co.com.foodcourt.usecase.exception.ValidationException;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ValidateRestaurant {

    private static final Pattern NAME_PATTERN = Pattern.compile("^(?!\\d+$).+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{1,13}$");
    private static final Pattern NIT_PATTERN = Pattern.compile("^\\d+$");



    public static void validatePhone(String phone) {
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException(ValidationMessages.INVALID_PHONE.getMessage());
        }
    }

    public static void validateNit(Long nit) {
        if (nit == null || !NIT_PATTERN.matcher(nit.toString()).matches()) {
            throw new ValidationException(ValidationMessages.INVALID_NIT.getMessage());
        }
    }

    public static void validateName(String name) {
        if (name == null || !NAME_PATTERN.matcher(name).matches()) {
            throw new ValidationException(ValidationMessages.INVALID_RESTAURANT_NAME.getMessage());
        }
    }


    public static void validateRestaurant(Restaurant restaurant) {
        if (restaurant == null) {
            throw new ValidationException(ValidationMessages.INVALID_RESTAURANT.getMessage());
        }
        validateNit(restaurant.getNit());
        validatePhone(restaurant.getPhone());
        validateName(restaurant.getName());
    }


}
