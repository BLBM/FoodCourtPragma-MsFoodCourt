package co.com.foodcourt.usecase.traceability_recorder;

import co.com.foodcourt.model.actor.Actor;
import co.com.foodcourt.model.actor.ActorRole;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderStatus;
import co.com.foodcourt.model.order.OrderTrace;
import co.com.foodcourt.model.order.gateways.TraceabilityService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class TraceabilityRecorderUseCase {

    private final TraceabilityService traceabilityService;

    public void recordTrace(Order order, OrderStatus previousStatus, OrderStatus newStatus, Actor actor) {
            OrderTrace trace = buildTrace(order, previousStatus, newStatus, actor);
            traceabilityService.saveTrace(trace);
    }

    private OrderTrace buildTrace(Order order, OrderStatus previousStatus, OrderStatus newStatus, Actor actor) {
        boolean isClient = actor.getRole() == ActorRole.CLIENT;
        boolean isEmployee = actor.getRole() == ActorRole.EMPLOYEE;

        return OrderTrace.builder()
                .orderId(order.getOrderId())
                .clientId(String.valueOf(order.getClientId()))
                .clientEmail(isClient ? actor.getEmail() : null)
                .dateTime(LocalDateTime.now())
                .prevStatus(previousStatus != null ? previousStatus.name() : null)
                .newStatus(newStatus.name())
                .employeeId(order.getChef() != null ? order.getChef() : null)
                .employeeEmail(isEmployee ? actor.getEmail() : null)
                .build();
    }
}
