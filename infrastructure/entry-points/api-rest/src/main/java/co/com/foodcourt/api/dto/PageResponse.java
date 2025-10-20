package co.com.foodcourt.api.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        int totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious,
        boolean first,
        boolean last
) {
}
