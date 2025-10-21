package co.com.foodcourt.model.order;
import co.com.foodcourt.model.restaurant.Restaurant;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Order {

    private Long orderId;
    private Long clientId;
    private LocalDateTime creationDate;
    private LocalDateTime deliveredDate;
    private OrderStatus status;
    private Long chef;
    private Restaurant restaurant;
    private List<OrderDish> dishes;
    private String securityPin;
}
