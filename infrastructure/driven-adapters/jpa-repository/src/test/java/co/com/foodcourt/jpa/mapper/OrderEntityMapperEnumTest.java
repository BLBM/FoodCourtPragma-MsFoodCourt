package co.com.foodcourt.jpa.mapper;



import co.com.foodcourt.jpa.entity.OrderStatusEntity;
import co.com.foodcourt.model.order.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderEntityMapperEnumTest {

    private OrderEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrderEntityMapperImpl();
    }


    @Test
    void shouldMapDomainStatusToEntityStatus() {
        OrderStatusEntity result = mapper.toEntityStatus(OrderStatus.PENDING);

        assertNotNull(result);
        assertEquals(OrderStatusEntity.PENDING, result);
    }

    @Test
    void shouldMapDomainStatusListToEntityStatusList() {
        List<OrderStatus> domainStatuses = List.of(OrderStatus.PENDING, OrderStatus.READY);

        List<OrderStatusEntity> result = mapper.toEntityStatusList(domainStatuses);

        assertEquals(2, result.size());
        assertEquals(OrderStatusEntity.PENDING, result.get(0));
        assertEquals(OrderStatusEntity.READY, result.get(1));
    }

    @Test
    void shouldMapEntityStatusToDomainStatus() {
        OrderStatus result = mapper.toDomainStatus(OrderStatusEntity.DELIVERED);

        assertNotNull(result);
        assertEquals(OrderStatus.DELIVERED, result);
    }
}