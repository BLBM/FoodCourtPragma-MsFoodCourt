package co.com.foodcourt.usecase.common;

import lombok.Getter;

@Getter
public enum ValidationMessages {

    INVALID_PHONE("Invalid phone number. It must be up to 13 digits and may start with +"),
    INVALID_RESTAURANT("Restaurant object is null"),
    INVALID_USER("User object is null"),
    INVALID_RESTAURANT_NAME("Restaurant name is null"),
    INVALID_ROL_OWNER_RESTAURANT("The user isn´t the owner"),
    INVALID_NIT("Invalid document ID. Only numeric values are allowed");

    private final String message;

    ValidationMessages(final String message) {
        this.message = message;
    }

}
