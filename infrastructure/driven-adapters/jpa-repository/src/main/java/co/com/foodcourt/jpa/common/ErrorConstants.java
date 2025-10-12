package co.com.foodcourt.jpa.common;

import lombok.Getter;

@Getter
public enum ErrorConstants {

    RESTAURANT_NOT_FOUND("Restaurant with id {}, not found");

    private final String message;

    ErrorConstants(String message) {
        this.message = message;
    }

}
