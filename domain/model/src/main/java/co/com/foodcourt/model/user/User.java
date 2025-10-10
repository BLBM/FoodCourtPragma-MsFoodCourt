package co.com.foodcourt.model.user;
import lombok.*;




@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long userId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String role;
}
