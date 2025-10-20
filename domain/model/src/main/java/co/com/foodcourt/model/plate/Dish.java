package co.com.foodcourt.model.plate;
import co.com.foodcourt.model.category.Category;
import co.com.foodcourt.model.restaurant.Restaurant;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Dish {

    private Long dishId;
    private String name;
    private Integer price;
    private Restaurant restaurant;
    private String description;
    private String urlImage;
    private Category category;
    private Boolean active;


}
