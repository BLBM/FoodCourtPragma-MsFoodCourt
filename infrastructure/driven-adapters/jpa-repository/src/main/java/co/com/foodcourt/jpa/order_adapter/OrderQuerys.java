package co.com.foodcourt.jpa.order_adapter;

public final class OrderQuerys {

    private OrderQuerys(){}

    public static final String EXISTS_BY_CLIENT_ID_AND_STATUS_IN =
            """
            SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END
            FROM OrderEntity o
            WHERE o.clientId = :clientId
              AND o.status IN :statuses
            """;
}
