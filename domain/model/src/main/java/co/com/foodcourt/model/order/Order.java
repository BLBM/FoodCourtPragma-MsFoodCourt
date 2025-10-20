package co.com.foodcourt.model.order;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.user.User;
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
    private OrderStatus status;
    private User chef;
    private Restaurant restaurant;
    private List<OrderDish> dishes;
}
