package co.com.foodcourt.usecase.util;

import co.com.foodcourt.model.actor.Actor;
import co.com.foodcourt.model.actor.ActorRole;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderStatus;
import co.com.foodcourt.usecase.common.ValidationMessages;
import co.com.foodcourt.usecase.exception.ValidationException;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class ValidationOrderStatus {


    public static boolean isCanceledByInvalidActor(OrderStatus newStatus, Actor actor) {
        return newStatus == OrderStatus.CANCELED && actor.getRole() != ActorRole.CLIENT;
    }

    public static boolean isInvalidCancelState(OrderStatus newStatus, OrderStatus current) {
        return newStatus == OrderStatus.CANCELED && current != OrderStatus.PENDING;
    }

    public static boolean requiresEmployeeRole(OrderStatus newStatus) {
        return newStatus == OrderStatus.PREPARATION
                || newStatus == OrderStatus.READY
                || newStatus == OrderStatus.DELIVERED;
    }


    public static void validatePreparationTransition(OrderStatus current) {
        if (current != OrderStatus.PENDING) {
            throw new ValidationException(ValidationMessages.INVALID_PREPARATION_TRANSITION.getMessage());
        }
    }

    public static void validateReadyTransition(Order order, OrderStatus current, Actor actor) {
        if (current != OrderStatus.PREPARATION) {
            throw new ValidationException(ValidationMessages.INVALID_READY_TRANSITION.getMessage());
        }
        if (!order.getChef().equals(actor.getId())) {
            throw new ValidationException(ValidationMessages.INVALID_READY_EMPLOYEE.getMessage());
        }
    }

    public static void validateDeliveredTransition(Order order, OrderStatus current, Actor actor, String pin) {
        if (current != OrderStatus.READY) {
            throw new ValidationException(ValidationMessages.INVALID_DELIVERED_TRANSITION.getMessage());
        }
        if (!order.getChef().equals(actor.getId())) {
            throw new ValidationException(ValidationMessages.INVALID_DELIVERED_EMPLOYEE.getMessage());
        }
        if (pin == null || !pin.equals(order.getSecurityPin())) {
            throw new ValidationException(ValidationMessages.INVALID_PIN.getMessage());
        }
    }

}
