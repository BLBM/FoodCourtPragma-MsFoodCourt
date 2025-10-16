package co.com.foodcourt.api.common;

public class SwaggerConstants {

    public static final String TAG_DISHES_CONTROLLER = "Endpoints for dish management operations.";
    public static final String TAG_RESTAURANT_CONTROLLER = "Operations related to restaurant management.";

    public static final String CREATE_RESTAURANT_SUMMARY = "Create a new owner user";
    public static final String CREATE_RESTAURANT_DESCRIPTION = """
        Creates a new restaurant in the system
        Only users with the ADMIN role are authorized to perform this operation.
        The owner of the restaurant must be provided in the request body.
        """;

    public static final String UPDATE_SUMMARY = "Update an existing dish";
    public static final String UPDATE_DESCRIPTION = """
            Updates the details of an existing dish (e.g., description, price, active state).
            Only users with the OWNER role are authorized to perform this operation.
            """;

    public static final String CREATE_DISH_DESCRIPTION = """
            Creates a new dish associated with a restaurant.
            Only users with the OWNER role are authorized to perform this operation.
            """;
    public static final String CREATE_DISH_SUMMARY = "Create a new dish";

    public static final String USER_ROLE_DESCRIPTION = "Role of the authenticated user";


}
