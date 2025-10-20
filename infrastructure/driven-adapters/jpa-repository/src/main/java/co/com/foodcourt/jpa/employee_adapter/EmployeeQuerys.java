package co.com.foodcourt.jpa.employee_adapter;

public final class EmployeeQuerys {

    private EmployeeQuerys(){}

    public static final String FIND_RESTAURANT_BY_EMPLOYEE_ID=
        """
        SELECT er.restaurant.restaurantId
        FROM EmployeeRestaurantEntity er
        WHERE er.employeeId = :employeeId
        """;

}
