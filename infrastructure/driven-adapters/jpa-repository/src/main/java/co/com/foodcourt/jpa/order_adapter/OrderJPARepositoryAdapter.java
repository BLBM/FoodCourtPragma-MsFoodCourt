package co.com.foodcourt.jpa.order_adapter;


import co.com.foodcourt.jpa.common.LogConstants;
import co.com.foodcourt.jpa.entity.OrderEntity;
import co.com.foodcourt.jpa.entity.OrderStatusEntity;
import co.com.foodcourt.jpa.mapper.OrderEntityMapper;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.model.order.OrderStatus;
import co.com.foodcourt.model.order.gateways.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderJPARepositoryAdapter implements OrderRepository {

    private final OrderJPARepository repository;
    private final OrderEntityMapper mapper;

    @Override
    @Transactional
    public Order saveOrder(Order order) {
        log.info(LogConstants.SAVE_ORDER.getMessage(),
                order.getClientId(), order.getRestaurant().getRestaurantId());

        OrderEntity orderEntity = mapper.toEntity(order);

        if (orderEntity.getOrderDishes() != null) {
            orderEntity.getOrderDishes().forEach(od -> od.setOrder(orderEntity));
        }

        OrderEntity savedEntity = repository.save(orderEntity);

        log.info(LogConstants.SAVE_ORDER_SUCCESS.getMessage(), savedEntity.getOrderId());
        return mapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByClientIdAndStatusIn(Long clientId, List<OrderStatus> statuses) {
        log.debug(LogConstants.CHECK_ORDERS_CLIENT.getMessage(), clientId, statuses);
        return repository.existsByClientIdAndStatusIn(
                clientId,
                mapper.toEntityStatusList(statuses)
        );
    }

    @Override
    public List<Order> findByRestaurantAndStatus(Long restaurantId, String status) {
        OrderStatusEntity statusEntity = OrderStatusEntity.valueOf(status.toUpperCase());
        List<OrderEntity> entities = repository
                .findOrdersByRestaurantAndStatusWithDetails(restaurantId, statusEntity);
        return entities.stream().map(mapper::toDomain).toList();
    }
}
