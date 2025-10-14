package co.com.foodcourt.api.common;

import lombok.Getter;

@Getter
public enum LogConstants {

    CREATE_RESTAURANT_REQUEST("Request to create restaurant: {}"),
    CREATE_RESTAURANT_SUCCESS("restaurant created successfully with name: {}"),
    CREATE_DISH_REQUEST("Request to create dish: {}"),
    CREATE_DISH_SUCCESS("Dish created successfully with name: {}"),
    UPDATE_DISH_REQUEST("Request to update dish with id: {}"),
    UPDATE_DISH_SUCCESS("Dish updated successfully with id: {}"),
    TIMESTAMP("timestamp:"),
    ERROR("error:"),
    OWNER("OWNER"),
    DETAILS("details:");

    private final String message;

    LogConstants(String message){this.message = message;}
}
