package co.com.foodcourt.model.order;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderTrace {
    private Long orderId;
    private String clientId;
    private String clientEmail;
    private LocalDateTime dateTime;
    private String prevStatus;
    private String newStatus;
    private Long employeeId;
    private String employeeEmail;
}
