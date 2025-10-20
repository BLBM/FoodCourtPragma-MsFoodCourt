package co.com.foodcourt.model.order;

import co.com.foodcourt.model.plate.Dish;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderDish {
    private Dish dish;
    private Integer quantity;
}
