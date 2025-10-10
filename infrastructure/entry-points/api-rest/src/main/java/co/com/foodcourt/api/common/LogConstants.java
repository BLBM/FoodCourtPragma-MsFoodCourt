package co.com.foodcourt.api.common;

import lombok.Getter;

@Getter
public enum LogConstants {

    CREATE_RESTAURANT_REQUEST("Request to create restaurant: {}"),
    CREATE_RESTAURANT_SUCCESS("restaurant created successfully with name: {}"),
    TIMESTAMP("timestamp:"),
    ERROR("error:"),
    DETAILS("details:");

    private final String message;

    LogConstants(String message){this.message = message;}
}
