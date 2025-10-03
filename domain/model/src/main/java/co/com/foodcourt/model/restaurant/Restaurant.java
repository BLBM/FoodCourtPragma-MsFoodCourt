package co.com.foodcourt.model.restaurant;
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
    private Long ownerId;
    private String urlLogo;
    private String phone;
    private Long nit;

}
