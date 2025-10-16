package co.com.foodcourt.api.controller;


import co.com.foodcourt.api.dto.PageResponse;
import co.com.foodcourt.api.service.PageableService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PageableServiceTest {

    @MockitoBean
    private PageableService pageableService;


    @Test
    void shouldPaginateSuccessfullyWhenPageIsWithinRange() {
        List<String> items = List.of("A", "B", "C", "D", "E");
        Function<String, String> mapper = s -> s + "_mapped";


        PageResponse<String> response = pageableService.paginate(items, 1, 2, mapper);

        assertEquals(2, response.content().size());
        assertEquals(List.of("A_mapped", "B_mapped"), response.content());
        assertEquals(1, response.page());
        assertEquals(2, response.size());
        assertEquals(5, response.totalElements());
        assertEquals(3, response.totalPages());
        assertTrue(response.hasNext());
        assertFalse(response.hasPrevious());
    }

    @Test
    void shouldHandleLastPageCorrectly() {
        List<String> items = List.of("A", "B", "C", "D", "E");
        Function<String, String> mapper = s -> s;

        PageResponse<String> response = pageableService.paginate(items, 3, 2, mapper);

        assertEquals(List.of("E"), response.content());
        assertEquals(3, response.page());
        assertTrue(response.hasPrevious());
        assertFalse(response.hasNext());
    }

    @Test
    void shouldHandleEmptyList() {
        List<String> items = List.of();
        Function<String, String> mapper = s -> s;

        PageResponse<String> response = pageableService.paginate(items, 1, 5, mapper);

        assertNotNull(response.content());
        assertTrue(response.content().isEmpty());
        assertEquals(1, response.page());
        assertEquals(0, response.totalElements());
        assertEquals(0, response.totalPages());
    }

    @Test
    void shouldClampPageNumberWhenOutOfBounds() {
        List<String> items = List.of("A", "B", "C");
        Function<String, String> mapper = s -> s;

        PageResponse<String> response = pageableService.paginate(items, 10, 2, mapper);

        assertEquals(2, response.page()); // should clamp to last valid page
        assertEquals(List.of("C"), response.content());
        assertTrue(response.hasPrevious());
    }

    @Test
    void shouldHandlePageSizeLargerThanTotalElements() {
        List<String> items = List.of("A", "B", "C");
        Function<String, String> mapper = s -> s;

        PageResponse<String> response = pageableService.paginate(items, 1, 10, mapper);


        assertEquals(List.of("A", "B", "C"), response.content());
        assertEquals(1, response.totalPages());
    }
}
