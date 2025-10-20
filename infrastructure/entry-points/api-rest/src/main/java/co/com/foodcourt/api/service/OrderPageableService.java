package co.com.foodcourt.api.service;

import co.com.foodcourt.api.dto.GetAllOrdersData;
import co.com.foodcourt.api.dto.OrderDishData;
import co.com.foodcourt.api.dto.PageResponse;
import co.com.foodcourt.model.order.Order;
import co.com.foodcourt.usecase.order.OrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderPageableService {
    private final OrderUseCase getOrdersByStatusUseCase;
    private final PageableService pageableService;

    public PageResponse<GetAllOrdersData> getOrders(Long employeeId, String status, int page, int size) {

        List<Order> orders = getOrdersByStatusUseCase.getAllOrderByStatus(employeeId, status);

        return pageableService.paginate(
                orders,
                page,
                size,
                o -> new GetAllOrdersData(
                        o.getOrderId(),
                        o.getClientId(),
                        o.getCreationDate(),
                        o.getStatus(),
                        o.getRestaurant().getName(),
                        o.getDishes().stream()
                                .map(d -> new OrderDishData(
                                        d.getDish().getDishId(),
                                        d.getDish().getName(),
                                        d.getQuantity()
                                ))
                                .toList()
                )
        );
    }
}
