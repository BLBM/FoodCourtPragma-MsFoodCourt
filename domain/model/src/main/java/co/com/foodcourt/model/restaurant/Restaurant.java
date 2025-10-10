package co.com.foodcourt.model.restaurant;
import co.com.foodcourt.model.user.User;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Restaurant {

    private Long restaurantId;
    private String name;
    private String address;
    private User owner;
    private String urlLogo;
    private String phone;
    private Long nit;

}
