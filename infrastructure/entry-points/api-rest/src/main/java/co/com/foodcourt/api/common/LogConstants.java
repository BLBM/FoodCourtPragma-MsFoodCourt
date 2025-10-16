package co.com.foodcourt.api.common;

import lombok.Getter;

@Getter
public enum LogConstants {

    CREATE_RESTAURANT_REQUEST("Request to create restaurant: {}"),
    CREATE_RESTAURANT_SUCCESS("restaurant created successfully with name: {}"),
    GET_ALL_RESTAURANT_REQUEST("Get all restaurants request received"),
    GET_ALL_RESTAURANT_SUCCESS("Get all restaurants successfully"),
    CREATE_DISH_REQUEST("Request to create dish: {}"),
    CREATE_DISH_SUCCESS("Dish created successfully with name: {}"),
    UPDATE_DISH_REQUEST("Request to update dish with id: {}"),
    UPDATE_DISH_SUCCESS("Dish updated successfully with id: {}"),
    GET_ALL_DISHES_REQUEST("Get all dishes request received"),
    GET_ALL_DISHES_SUCCESS("Get all dishes successfully"),
    TIMESTAMP("timestamp:"),
    ERROR("error:"),
    OWNER("OWNER"),
    DETAILS("details:");

    private final String message;

    LogConstants(String message){this.message = message;}
}
