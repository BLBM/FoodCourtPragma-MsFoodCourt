package co.com.foodcourt.usecase.update_order;

import co.com.foodcourt.model.actor.Actor;
import co.com.foodcourt.model.actor.ActorRole;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderStatus;
import co.com.foodcourt.usecase.common.ValidationMessages;
import co.com.foodcourt.usecase.exception.ValidationException;
import co.com.foodcourt.usecase.util.ValidationOrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationOrderStatusTest {

    private Order order;
    private Actor employee;
    private Actor otherEmployee;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setOrderId(1L);
        order.setChef(10L);
        order.setSecurityPin("1234");

        employee = new Actor(10L, ActorRole.EMPLOYEE, "chef@mail.com");
        otherEmployee = new Actor(20L, ActorRole.EMPLOYEE, "other@mail.com");
    }

    @Test
    void shouldThrowWhenInvalidPreparationTransition() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                ValidationOrderStatus.validatePreparationTransition(OrderStatus.READY)
        );

        assertEquals(ValidationMessages.INVALID_PREPARATION_TRANSITION.getMessage(), ex.getMessage());
    }

    @Test
    void shouldThrowWhenInvalidReadyTransitionState() {
        order.setStatus(OrderStatus.PENDING);
        ValidationException ex = assertThrows(ValidationException.class, () ->
                ValidationOrderStatus.validateReadyTransition(order, OrderStatus.PENDING, employee)
        );

        assertEquals(ValidationMessages.INVALID_READY_TRANSITION.getMessage(), ex.getMessage());
    }

    @Test
    void shouldThrowWhenDifferentEmployeeMarksReady() {
        order.setStatus(OrderStatus.PREPARATION);
        ValidationException ex = assertThrows(ValidationException.class, () ->
                ValidationOrderStatus.validateReadyTransition(order, OrderStatus.PREPARATION, otherEmployee)
        );

        assertEquals(ValidationMessages.INVALID_READY_EMPLOYEE.getMessage(), ex.getMessage());
    }


    @Test
    void shouldThrowWhenInvalidDeliveredTransitionByState() {
        order.setStatus(OrderStatus.PREPARATION);
        ValidationException ex = assertThrows(ValidationException.class, () ->
                ValidationOrderStatus.validateDeliveredTransition(order, OrderStatus.PREPARATION, employee, "1234")
        );

        assertEquals(ValidationMessages.INVALID_DELIVERED_TRANSITION.getMessage(), ex.getMessage());
    }

    @Test
    void shouldThrowWhenInvalidDeliveredEmployee() {
        order.setStatus(OrderStatus.READY);
        ValidationException ex = assertThrows(ValidationException.class, () ->
                ValidationOrderStatus.validateDeliveredTransition(order, OrderStatus.READY, otherEmployee, "1234")
        );

        assertEquals(ValidationMessages.INVALID_DELIVERED_EMPLOYEE.getMessage(), ex.getMessage());
    }


}
