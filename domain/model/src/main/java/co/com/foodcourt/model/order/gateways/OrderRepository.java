package co.com.foodcourt.model.order.gateways;

import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderStatus;

import java.util.List;

public interface OrderRepository {
    Order saveOrder(Order order);
    boolean existsByClientIdAndStatusIn(Long clientId, List<OrderStatus> statuses);
    List<Order> findByRestaurantAndStatus(Long restaurantId, String status);
}
