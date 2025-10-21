package co.com.foodcourt.usecase.update_order;

import co.com.foodcourt.model.actor.Actor;
import co.com.foodcourt.model.actor.ActorRole;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderStatus;
import co.com.foodcourt.model.order.gateways.NotificationService;
import co.com.foodcourt.model.order.gateways.OrderRepository;
import co.com.foodcourt.usecase.common.NotificationMessages;
import co.com.foodcourt.usecase.common.ValidationMessages;
import co.com.foodcourt.usecase.exception.ValidationException;
import co.com.foodcourt.usecase.traceability_recorder.TraceabilityRecorderUseCase;
import co.com.foodcourt.usecase.util.ValidationOrderStatus;
import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class UpdateOrderUseCase {

    private final OrderRepository orderRepository;
    private final TraceabilityRecorderUseCase traceabilityRecorderUseCase;
    private final NotificationService notificationService;

    public Order updateStatus(Long orderId, OrderStatus newStatus, Actor actor, String pin){

        Order order= orderRepository.findById(orderId);
        OrderStatus previousStatus = order.getStatus();

        validateTransition(order, newStatus, actor, pin);
        applyStatusChanges(order, newStatus, actor);

        if (newStatus == OrderStatus.READY) {
            generateAndAssignPin(order);
        }

        orderRepository.saveOrder(order);

        traceabilityRecorderUseCase.recordTrace(order,previousStatus,newStatus,actor);

        if (newStatus == OrderStatus.READY) {
            sendReadyNotification(order);
        }

        return order;
    }


    private void validateTransition(Order order, OrderStatus newStatus, Actor actor, String pin) {
        OrderStatus current = order.getStatus();

        if (ValidationOrderStatus.isCanceledByInvalidActor(newStatus, actor)) {
            throw new ValidationException(ValidationMessages.INVALID_CANCEL_ROLE.getMessage());
        }

        if (ValidationOrderStatus.isInvalidCancelState(newStatus, current)) {
            throw new ValidationException(ValidationMessages.INVALID_CANCEL_STATE.getMessage());
        }

        if (ValidationOrderStatus.requiresEmployeeRole(newStatus) && actor.getRole() != ActorRole.EMPLOYEE) {
            throw new ValidationException(ValidationMessages.INVALID_EMPLOYEE_ROLE.getMessage());
        }

        switch (newStatus) {
            case PREPARATION -> ValidationOrderStatus.validatePreparationTransition(current);
            case READY -> ValidationOrderStatus.validateReadyTransition(order, current, actor);
            case DELIVERED -> ValidationOrderStatus.validateDeliveredTransition(order, current, actor, pin);
            default -> throw new ValidationException(ValidationMessages.INVALID_STATUS.getMessage());
        }
    }

    private void applyStatusChanges(Order order, OrderStatus newStatus, Actor actor) {
        if (newStatus == OrderStatus.PREPARATION) {
            order.setChef(actor.getId());
        }

        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveredDate(LocalDateTime.now());
        }

        order.setStatus(newStatus);
    }

    private void generateAndAssignPin(Order order) {
        SecureRandom random = new SecureRandom();
        String pin = String.format("%04d", random.nextInt(10000));
        order.setSecurityPin(pin);
    }


    private void sendReadyNotification(Order order) {

        String msg = NotificationMessages.ORDER_READY.format(
                order.getOrderId(),
                order.getSecurityPin()
        );

        notificationService.sendOrderReadyNotification(msg);

    }

}
