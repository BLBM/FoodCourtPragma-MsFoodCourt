package co.com.foodcourt.jpa.dishadapter;

public final class DishQueries {

    private DishQueries() {}

    public static final String FIND_BY_RESTAURANT_NAME = """
        SELECT d
        FROM DishEntity d
        JOIN FETCH d.restaurantId r
        JOIN FETCH d.categoryId c
        WHERE r.restaurantId = :restaurantId
            AND d.active = TRUE
        ORDER BY d.name ASC
   """;

    public static final String FIND_BY_RESTAURANT_NAME_AND_CATEGORY_ID = """
        SELECT d
        FROM DishEntity d
        JOIN FETCH d.restaurantId r
        JOIN FETCH d.categoryId c
        WHERE r.restaurantId = :restaurantId
          AND c.categoryId = :categoryId
          AND d.active = TRUE
        ORDER BY d.name ASC
   """;
}
