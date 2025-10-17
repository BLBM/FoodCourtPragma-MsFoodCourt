package co.com.foodcourt.usecase.order;

import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderDish;
import co.com.foodcourt.model.order.OrderStatus;
import co.com.foodcourt.model.order.gateways.OrderRepository;
import co.com.foodcourt.model.plate.gateways.DishRepository;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.restaurant.gateways.RestaurantRepository;
import co.com.foodcourt.usecase.util.ValidateOrder;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class OrderUseCase {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;


    public Order createOrder(Long clientId, Long restaurantId, List<OrderDish> orderDishes){

        ValidateOrder.validateOrderCreation(
                clientId,
                restaurantId,
                orderDishes,
                orderRepository,
                dishRepository,
                restaurantRepository
        );

        Restaurant restaurant = restaurantRepository.getRestaurantById(restaurantId);

        Order order = Order.builder()
                .clientId(clientId)
                .restaurant(restaurant)
                .dishes(orderDishes)
                .status(OrderStatus.PENDING)
                .build();

        return orderRepository.saveOrder(order);
    }
}
