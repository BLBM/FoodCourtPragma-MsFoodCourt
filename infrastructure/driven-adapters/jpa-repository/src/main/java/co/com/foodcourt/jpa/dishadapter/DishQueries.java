package co.com.foodcourt.jpa.dishadapter;

public final class DishQueries {

    private DishQueries() {}

    public static final String FIND_BY_RESTAURANT_NAME = """
        SELECT d
        FROM DishEntity d
        JOIN FETCH d.restaurantId r
        JOIN FETCH d.categoryId c
        WHERE LOWER(r.name) = LOWER(:restaurantName)
        ORDER BY d.name ASC
   """;

    public static final String FIND_BY_RESTAURANT_NAME_AND_CATEGORY_ID = """
        SELECT d
        FROM DishEntity d
        JOIN FETCH d.restaurantId r
        JOIN FETCH d.categoryId c
        WHERE LOWER(r.name) = LOWER(:restaurantName)
          AND c.categoryId = :categoryId
        ORDER BY d.name ASC
   """;
}
