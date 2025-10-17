package co.com.foodcourt.jpa.order_adapter;

import co.com.foodcourt.jpa.entity.OrderEntity;
import co.com.foodcourt.jpa.entity.OrderStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderJPARepository extends JpaRepository <OrderEntity,Long>{
    @Query(OrderQuerys.EXISTS_BY_CLIENT_ID_AND_STATUS_IN)
    boolean existsByClientIdAndStatusIn(Long clientId, List<OrderStatusEntity> statuses);
}
