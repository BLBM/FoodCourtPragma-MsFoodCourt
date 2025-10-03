package co.com.foodcourt.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "restaurants")
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "address", nullable = false, length = 75)
    private String address;

    @Column(name = "ownerId", nullable = false)
    private Long ownerId;

    @Column(name = "urlLogo", nullable = false)
    private String urlLogo;

    @Column(name = "phone", nullable = false, length = 13)
    private String phone;

    @Column(name = "nit", nullable = false)
    private Long nit;
}
