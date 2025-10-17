package co.com.foodcourt.api.common;

import lombok.Getter;

@Getter
public enum ErrorMessages {
    INVALID_ROL_OWNER_DISHES("Only owners can create dishes"),
    INVALID_ROL_OWNER_UPDATE_DISHES("Only owners can update dishes"),
    INVALID_RESTAURANT_ROL("Only admin can create restaurants"),
    INVALID_ROL_SHOW_RESTAURANTS("Only clients can to see the restaurants please register"),
    INVALID_ROL_ASSIGN_EMPLOYEE("Only owners can assign employees");

    private final String message;

    ErrorMessages(String message){this.message = message;}

}
