package co.com.foodcourt.usecase.create_order;

import co.com.foodcourt.model.actor.Actor;
import co.com.foodcourt.model.employee_restaurant.gateways.EmployeeRestaurantRepository;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderStatus;
import co.com.foodcourt.model.order.gateways.OrderRepository;
import co.com.foodcourt.model.plate.gateways.DishRepository;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.restaurant.gateways.RestaurantRepository;
import co.com.foodcourt.usecase.common.ValidationMessages;
import co.com.foodcourt.usecase.exception.ValidationException;
import co.com.foodcourt.usecase.traceability_recorder.TraceabilityRecorderUseCase;
import co.com.foodcourt.usecase.util.ValidateOrder;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;
    private final EmployeeRestaurantRepository employeeRestaurantRepository;
    private final TraceabilityRecorderUseCase traceabilityRecorderUseCase;


    public Order createOrder(Order orderRequest, Actor actor){

        ValidateOrder.validateOrderCreation(
                actor.getId(),
                orderRequest,
                orderRepository,
                dishRepository,
                restaurantRepository
        );

        Restaurant restaurant = restaurantRepository.getRestaurantById(orderRequest.getRestaurant().getRestaurantId());

        Order order = Order.builder()
                .clientId(actor.getId())
                .restaurant(restaurant)
                .dishes(orderRequest.getDishes())
                .status(OrderStatus.PENDING)
                .creationDate(LocalDateTime.now())
                .build();

        traceabilityRecorderUseCase.recordTrace(order,null,OrderStatus.PENDING,actor);
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
