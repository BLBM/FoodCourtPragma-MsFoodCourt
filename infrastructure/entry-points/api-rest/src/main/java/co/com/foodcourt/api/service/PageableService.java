package co.com.foodcourt.api.service;

import co.com.foodcourt.api.dto.PageResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
public class PageableService {

    public <T, R> PageResponse<R> paginate(List<T> items, int page, int size, Function<T, R> mapper) {
        int totalElements = items.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int currentPage = Math.max(1, Math.min(page, totalPages == 0 ? 1 : totalPages));

        int fromIndex = Math.min((currentPage - 1) * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<R> content = items.subList(fromIndex, toIndex)
                .stream()
                .map(mapper)
                .toList();

        return new PageResponse<>(
                content,
                currentPage,
                size,
                totalElements,
                totalPages,
                currentPage < totalPages,
                currentPage > 1,
                currentPage == 1,
                currentPage >= totalPages
        );
    }
}
