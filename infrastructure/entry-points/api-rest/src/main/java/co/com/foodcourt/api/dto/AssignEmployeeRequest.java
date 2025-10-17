package co.com.foodcourt.api.dto;

import jakarta.validation.constraints.NotNull;

public record AssignEmployeeRequest(
        @NotNull(message = "employeeId is required")
        Long employeeId
) {
}
