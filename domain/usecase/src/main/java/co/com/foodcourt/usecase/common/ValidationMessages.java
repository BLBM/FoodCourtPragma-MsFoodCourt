package co.com.foodcourt.usecase.common;

import lombok.Getter;

@Getter
public enum ValidationMessages {

    INVALID_PHONE("Invalid phone number. It must be up to 13 digits and may start with +"),
    INVALID_RESTAURANT("Restaurant object is null"),
    INVALID_RESTAURANT_NAME("Restaurant id is null"),
    INVALID_ROL_OWNER_RESTAURANT("The user isn´t the owner"),
    INVALID_NIT("Invalid document ID. Only numeric values are allowed"),
    INVALID_DISH_PRICE("Invalid dish its must bea an integer and greater than zero "),
    INVALID_DISH("Dish object is null"),
    INVALID_OWNER_OF_RESTAURANT("This user isn´t owner from this restaurant "),
    INVALID_EMPLOYEE("This user does not have rol of employee"),
    INVALID_YET_ORDER_IN_PROCESS("This client has a order in process"),
    INVALID_DISHES_FOR_RESTAURANT("The following dishes do not belong to the restaurant with id %s: %s"),
    DISHES_NOT_FOUND_FOR_RESTAURANT("No dishes found for restaurant: {} "),
    INVALID_RESTAURANT_PARAM("Restaurant name must be provided"),
    INVALID_EMPLOYEE_NOT_BELONG("The employee does not belong to any restaurant.");

    private final String message;

    ValidationMessages(final String message) {
        this.message = message;
    }

}
