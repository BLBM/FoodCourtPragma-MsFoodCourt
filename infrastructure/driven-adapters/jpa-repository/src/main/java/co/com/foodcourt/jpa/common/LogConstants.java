package co.com.foodcourt.jpa.common;

import lombok.Getter;

@Getter
public enum LogConstants {
    SAVE_RESTAURANT("Saving restaurant: {}"),
    RESTAURANT_SAVED("Restaurant saved successfully with id: {}"),
    SAVE_DISH("saving Dish : {}"),
    DISH_SAVED("Dish saved successfully with id: {}"),
    FIND_RESTAURANT_BY_ID("Searching restaurant by id: {}"),
    RESTAURANT_FOUND("Restaurant with id {} found"),
    SAVE_EMPLOYEE_RESTAURANT("Saving relation employee restaurant: {}"),
    EMPLOYEE_RESTAURANT_SAVED("Relation employee restaurant saved: {}"),
    SAVE_ORDER("Saving order for clientId={} and restaurantId={}"),
    SAVE_ORDER_SUCCESS("Order saved successfully with id={}"),
    CHECK_ORDERS_CLIENT("Checking if client {} has active orders in statuses {}"),
    FIND_ORDER_BY_ID("Searching for order with id [{}]"),
    FIND_ORDER_BY_ID_SUCCESS("Order [{}] retrieved successfully");


    private final String message;

    LogConstants(String message) {
        this.message = message;
    }

}
