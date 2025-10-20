package co.com.foodcourt.usecase.order;

import co.com.foodcourt.model.employee_restaurant.gateways.EmployeeRestaurantRepository;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderStatus;
import co.com.foodcourt.model.order.gateways.OrderRepository;
import co.com.foodcourt.model.plate.gateways.DishRepository;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.restaurant.gateways.RestaurantRepository;
import co.com.foodcourt.usecase.common.ValidationMessages;
import co.com.foodcourt.usecase.exception.ValidationException;
import co.com.foodcourt.usecase.util.ValidateOrder;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class OrderUseCase {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;
    private final EmployeeRestaurantRepository employeeRestaurantRepository;


    public Order createOrder(Order orderRequest, Long clientId){

        ValidateOrder.validateOrderCreation(
                clientId,
                orderRequest,
                orderRepository,
                dishRepository,
                restaurantRepository
        );

        Restaurant restaurant = restaurantRepository.getRestaurantById(orderRequest.getRestaurant().getRestaurantId());

        Order order = Order.builder()
                .clientId(clientId)
                .restaurant(restaurant)
                .dishes(orderRequest.getDishes())
                .status(OrderStatus.PENDING)
                .creationDate(LocalDateTime.now())
                .build();

        return orderRepository.saveOrder(order);
    }

    public List<Order> getAllOrderByStatus(Long employeeId, String status) {

        Long restaurantId = employeeRestaurantRepository.findRestaurantIdByEmployeeId(employeeId);

        if (restaurantId == null) {
            throw new ValidationException(ValidationMessages.INVALID_EMPLOYEE_NOT_BELONG.getMessage());
        }

        return orderRepository.findByRestaurantAndStatus(restaurantId, status);
    }
}
