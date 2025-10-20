package co.com.foodcourt.api.common;


import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SwaggerConstants {

    public static final String TAG_DISHES_CONTROLLER = "Endpoints for dish management operations.";
    public static final String TAG_RESTAURANT_CONTROLLER = "Operations related to restaurant management.";
    public static final String TAG_ORDERS_CONTROLLER = "Operations related to order management.";


    public static final String CREATE_RESTAURANT_SUMMARY = "Create a new owner user";
    public static final String CREATE_RESTAURANT_DESCRIPTION = """
        Creates a new restaurant in the system
        Only users with the ADMIN role are authorized to perform this operation.
        The owner of the restaurant must be provided in the request body.
        """;

    public static final String GET_ALL_RESTAURANTS_SUMMARY = "Get all restaurants existing";
    public static final String GET_ALL_RESTAURANTS_DESCRIPTION = """
        Return all restaurants creates by the admin
        with attributes (name and logo_url)
        """;

    public static final String ASSIGN_EMPLOYEE_SUMMARY = "Assign employee to restaurants existing";
    public static final String ASSIGN_EMPLOYEE_DESCRIPTION = """
        assign employee existing to a restaurants existing
        """;

    public static final String UPDATE_SUMMARY = "Update an existing dish";
    public static final String UPDATE_DESCRIPTION = """
            Updates the details of an existing dish (e.g., description, price, active state).
            Only users with the OWNER role are authorized to perform this operation.
            """;


    public static final String GET_ALL_DISHES_SUMMARY = "Get all dishes existing";
    public static final String GET_ALL_DISHES_DESCRIPTION = """
            Get all dishes actives in the restaurant filter by category_id and restaurant_id
            """;

    public static final String CREATE_DISH_DESCRIPTION = """
            Creates a new dish associated with a restaurant.
            Only users with the OWNER role are authorized to perform this operation.
            """;
    public static final String CREATE_DISH_SUMMARY = "Create a new dish";

    public static final String CREATE_ORDER_DESCRIPTION = "create a new order with dishes a quantities ";
    public static final String CREATE_ORDER_SUMMARY = "Create a new order";

    public static final String USER_ROLE_DESCRIPTION = "Role of the authenticated user";


}
