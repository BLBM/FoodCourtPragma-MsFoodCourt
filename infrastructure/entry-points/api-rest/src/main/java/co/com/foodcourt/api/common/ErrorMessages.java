package co.com.foodcourt.api.common;

import lombok.Getter;

@Getter
public enum ErrorMessages {
    INVALID_ROL_OWNER_DISHES("Only owner can create dishes");

    private final String message;

    ErrorMessages(String message){this.message = message;}

}
