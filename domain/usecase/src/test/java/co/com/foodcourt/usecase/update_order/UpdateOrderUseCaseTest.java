package co.com.foodcourt.usecase.update_order;

import co.com.foodcourt.model.actor.Actor;
import co.com.foodcourt.model.actor.ActorRole;

import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderDish;


import co.com.foodcourt.model.order.OrderStatus;
import co.com.foodcourt.model.order.gateways.NotificationService;
import co.com.foodcourt.model.order.gateways.OrderRepository;
import co.com.foodcourt.usecase.common.ValidationMessages;
import co.com.foodcourt.usecase.exception.ValidationException;
import co.com.foodcourt.usecase.traceability_recorder.TraceabilityRecorderUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateOrderUseCaseTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private TraceabilityRecorderUseCase traceabilityRecorderUseCase;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UpdateOrderUseCase updateOrderUseCase;

    private Order order;
    private Actor client;
    private Actor employee;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setOrderId(1L);
        order.setClientId(99L);
        order.setStatus(OrderStatus.PENDING);
        order.setDishes(List.of(new OrderDish()));

        client = new Actor(99L, ActorRole.CLIENT, "client@mail.com");
        employee = new Actor(10L, ActorRole.EMPLOYEE, "chef@mail.com");
    }

    @Test
    void shouldUpdateOrderToPreparationSuccessfully() {
        when(orderRepository.findById(1L)).thenReturn(order);

        Order result = updateOrderUseCase.updateStatus(1L, OrderStatus.PREPARATION, employee, null);

        assertEquals(OrderStatus.PREPARATION, result.getStatus());
        assertEquals(employee.getId(), result.getChef());
        verify(orderRepository).saveOrder(order);
        verify(traceabilityRecorderUseCase)
                .recordTrace(order, OrderStatus.PENDING, OrderStatus.PREPARATION, employee);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldUpdateOrderToReadyAndSendNotification() {
        order.setStatus(OrderStatus.PREPARATION);
        order.setChef(employee.getId());
        when(orderRepository.findById(1L)).thenReturn(order);

        Order result = updateOrderUseCase.updateStatus(1L, OrderStatus.READY, employee, null);

        assertEquals(OrderStatus.READY, result.getStatus());
        assertNotNull(result.getSecurityPin());
        assertEquals(4, result.getSecurityPin().length());
        verify(notificationService).sendOrderReadyNotification(anyString());
        verify(traceabilityRecorderUseCase)
                .recordTrace(order, OrderStatus.PREPARATION, OrderStatus.READY, employee);
    }

    @Test
    void shouldUpdateOrderToDeliveredSuccessfullyWithValidPin() {
        order.setStatus(OrderStatus.READY);
        order.setChef(employee.getId());
        order.setSecurityPin("1234");
        when(orderRepository.findById(1L)).thenReturn(order);

        Order result = updateOrderUseCase.updateStatus(1L, OrderStatus.DELIVERED, employee, "1234");

        assertEquals(OrderStatus.DELIVERED, result.getStatus());
        assertNotNull(result.getDeliveredDate());
        verify(orderRepository).saveOrder(order);
        verify(traceabilityRecorderUseCase)
                .recordTrace(order, OrderStatus.READY, OrderStatus.DELIVERED, employee);
    }


    @Test
    void shouldThrowExceptionWhenCancelingNonPendingOrder() {
        order.setStatus(OrderStatus.PREPARATION);
        when(orderRepository.findById(1L)).thenReturn(order);

        ValidationException ex = assertThrows(ValidationException.class, () ->
                updateOrderUseCase.updateStatus(1L, OrderStatus.CANCELED, client, null)
        );

        assertEquals(ValidationMessages.INVALID_CANCEL_STATE.getMessage(), ex.getMessage());
        verify(orderRepository, never()).saveOrder(any());
    }


    @Test
    void shouldThrowExceptionWhenEmployeeTriesToCancel() {
        when(orderRepository.findById(1L)).thenReturn(order);

        ValidationException ex = assertThrows(ValidationException.class, () ->
                updateOrderUseCase.updateStatus(1L, OrderStatus.CANCELED, employee, null)
        );

        assertEquals(ValidationMessages.INVALID_CANCEL_ROLE.getMessage(), ex.getMessage());
        verify(orderRepository, never()).saveOrder(any());
    }


    @Test
    void shouldThrowExceptionWhenDifferentEmployeeMarksReady() {
        order.setStatus(OrderStatus.PREPARATION);
        order.setChef(50L);
        when(orderRepository.findById(1L)).thenReturn(order);

        ValidationException ex = assertThrows(ValidationException.class, () ->
                updateOrderUseCase.updateStatus(1L, OrderStatus.READY, employee, null)
        );

        assertEquals(ValidationMessages.INVALID_READY_EMPLOYEE.getMessage(), ex.getMessage());
    }


    @Test
    void shouldThrowExceptionWhenInvalidPinOnDelivered() {
        order.setStatus(OrderStatus.READY);
        order.setChef(employee.getId());
        order.setSecurityPin("1234");
        when(orderRepository.findById(1L)).thenReturn(order);

        ValidationException ex = assertThrows(ValidationException.class, () ->
                updateOrderUseCase.updateStatus(1L, OrderStatus.DELIVERED, employee, "9999")
        );

        assertEquals(ValidationMessages.INVALID_PIN.getMessage(), ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPinIsNullOnDelivered() {
        order.setStatus(OrderStatus.READY);
        order.setChef(employee.getId());
        order.setSecurityPin("5678");
        when(orderRepository.findById(1L)).thenReturn(order);

        ValidationException ex = assertThrows(ValidationException.class, () ->
                updateOrderUseCase.updateStatus(1L, OrderStatus.DELIVERED, employee, null)
        );

        assertEquals(ValidationMessages.INVALID_PIN.getMessage(), ex.getMessage());
    }


}