package co.com.foodcourt.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response")
public class ErrorResponse {

    @Schema(description = "Time when the error occurred", example = "2025-10-14T10:25:00.543Z")
    private LocalDateTime timestamp;

    @Schema(description = "Detailed error message", example = "Error by exception")
    private String detail;
}
