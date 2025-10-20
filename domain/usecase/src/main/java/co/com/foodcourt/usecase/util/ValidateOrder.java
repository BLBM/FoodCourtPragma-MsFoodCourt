package co.com.foodcourt.usecase.util;

import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderDish;
import co.com.foodcourt.model.order.OrderStatus;
import co.com.foodcourt.model.order.gateways.OrderRepository;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.model.plate.gateways.DishRepository;
import co.com.foodcourt.model.restaurant.gateways.RestaurantRepository;
import co.com.foodcourt.usecase.common.ValidationMessages;
import co.com.foodcourt.usecase.exception.ValidationException;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ValidateOrder {

    public static void validateOrderCreation(
            Long clientId,
            Order order,
            OrderRepository orderRepository,
            DishRepository dishRepository,
            RestaurantRepository restaurantRepository
    ) {
        validateClientHasNoOrderInProcess(clientId, orderRepository);
        validateDishesBelongToRestaurant(order.getRestaurant().getRestaurantId(), order.getDishes(), dishRepository, restaurantRepository);
    }

    private static void validateClientHasNoOrderInProcess(Long clientId, OrderRepository orderRepository) {
        boolean hasOrderInProcess = orderRepository.existsByClientIdAndStatusIn(
                clientId,
                List.of(OrderStatus.PENDING, OrderStatus.PREPARATION, OrderStatus.READY)
        );
        if (hasOrderInProcess) {
            throw new ValidationException(ValidationMessages.INVALID_YET_ORDER_IN_PROCESS.getMessage());
        }
    }

    private static void validateDishesBelongToRestaurant(
            Long restaurantId,
            List<OrderDish> orderDishes,
            DishRepository dishRepository,
            RestaurantRepository restaurantRepository
    ) {

        restaurantRepository.getRestaurantById(restaurantId);

        Set<Long> validDishIds = dishRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(Dish::getDishId)
                .collect(Collectors.toSet());


        List<Long> invalidDishIds = orderDishes.stream()
                .map(od -> od.getDish().getDishId())
                .filter(dishId -> !validDishIds.contains(dishId))
                .toList();

        if (!invalidDishIds.isEmpty()) {
            String msg = String.format(
                    ValidationMessages.INVALID_DISHES_FOR_RESTAURANT.getMessage(),
                    restaurantId,
                    invalidDishIds
            );
            throw new ValidationException(msg);
        }
    }

}
