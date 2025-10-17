package co.com.foodcourt.model.employee_restaurant;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EmployeeRestaurant {
    private Long id;
    private Long employeeId;
    private Long restaurantId;
}
