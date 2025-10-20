package co.com.foodcourt.model.restaurant.exception;

public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException(String message, Object... args) {
        super(String.format(message.replace("{}", "%s"), args));
    }
}
