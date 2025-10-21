package co.com.foodcourt.model.actor;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Actor {

    private Long id;
    private ActorRole role;
    private String email;
}
