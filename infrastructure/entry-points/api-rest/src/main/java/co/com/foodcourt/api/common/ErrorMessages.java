package co.com.foodcourt.api.common;

import lombok.Getter;

@Getter
public enum ErrorMessages {
    INVALID_ROL_OWNER_DISHES("Only owners can create dishes"),
    INVALID_ROL_OWNER_UPDATE_DISHES("Only owners can update dishes"),
    INVALID_RESTAURANT_ROL("Only admin can create restaurants");

    private final String message;

    ErrorMessages(String message){this.message = message;}

}
