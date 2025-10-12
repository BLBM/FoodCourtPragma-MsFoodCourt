package co.com.foodcourt.model.category;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Category {

    private Long categoryId;
    private String name;
    private String description;

}
