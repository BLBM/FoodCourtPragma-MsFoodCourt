package co.com.foodcourt.jpa.employee_adapter;

import co.com.foodcourt.jpa.entity.EmployeeRestaurantEntity;
import co.com.foodcourt.jpa.entity.RestaurantEntity;
import co.com.foodcourt.model.employee_restaurant.EmployeeRestaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeRestaurantRepositoryAdapterTest {

    @Mock
    private EmployeeRestaurantJPARepository repository;

    @Mock
    private ObjectMapper mapper;

    private EmployeeRestaurantRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EmployeeRestaurantRepositoryAdapter(repository, mapper);
    }

    @Test
    void saveEmployeeRestaurant_shouldSaveSuccessfully() {
        Long employeeId = 1L;
        Long restaurantId = 100L;
        Long savedId = 999L;

        EmployeeRestaurant employeeRestaurant = EmployeeRestaurant.builder()
                .employeeId(employeeId)
                .restaurantId(restaurantId)
                .build();

        EmployeeRestaurantEntity mappedEntity = new EmployeeRestaurantEntity();
        mappedEntity.setEmployeeId(employeeId);

        EmployeeRestaurantEntity savedEntity = new EmployeeRestaurantEntity();
        savedEntity.setId(savedId);
        savedEntity.setEmployeeId(employeeId);

        when(mapper.map(employeeRestaurant, EmployeeRestaurantEntity.class))
                .thenReturn(mappedEntity);
        when(repository.save(any(EmployeeRestaurantEntity.class)))
                .thenReturn(savedEntity);

        adapter.saveEmployeeRestaurant(employeeRestaurant);

        verify(mapper).map(employeeRestaurant, EmployeeRestaurantEntity.class);

        ArgumentCaptor<EmployeeRestaurantEntity> entityCaptor =
                ArgumentCaptor.forClass(EmployeeRestaurantEntity.class);
        verify(repository).save(entityCaptor.capture());

        EmployeeRestaurantEntity capturedEntity = entityCaptor.getValue();
        assertNotNull(capturedEntity.getRestaurant());
        assertEquals(restaurantId, capturedEntity.getRestaurant().getRestaurantId());
        verify(repository, times(1)).save(any(EmployeeRestaurantEntity.class));
    }
    

}