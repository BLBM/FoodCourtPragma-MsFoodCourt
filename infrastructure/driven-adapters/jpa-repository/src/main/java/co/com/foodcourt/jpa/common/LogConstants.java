package co.com.foodcourt.jpa.common;

public enum LogConstants {
    SAVE_RESTAURANT("Saving restaurant: {}"),
    RESTAURANT_SAVED("Restaurant saved successfully with id: {}");


    private final String message;

    LogConstants(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
