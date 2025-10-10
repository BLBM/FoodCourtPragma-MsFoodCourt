package co.com.foodcourt.jpa.restaurantadapter;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;

import co.com.foodcourt.jpa.entity.RestaurantEntity;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class JPARepositoryAdapterTest {

    @Mock
    private JPARepository repository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private JPARepositoryAdapter adapter;

    private Restaurant restaurant;
    private RestaurantEntity restaurantEntity;
    private User owner;

    @BeforeEach
    void init() {
        owner = User.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        restaurant = Restaurant.builder()
                .restaurantId(123L)
                .name("Test Restaurant")
                .owner(owner)
                .build();

        restaurantEntity = RestaurantEntity.builder()
                .restaurantId(123L)
                .name("Test Restaurant")
                .ownerId(1L)
                .build();
    }

    @Test
    void testSave() {
        when(mapper.map(restaurant, RestaurantEntity.class)).thenReturn(restaurantEntity);
        when(repository.save(restaurantEntity)).thenReturn(restaurantEntity);
        when(mapper.map(restaurantEntity, Restaurant.class)).thenReturn(restaurant);

        Restaurant result = adapter.save(restaurant);

        assertEquals(restaurant, result);
        verify(repository).save(restaurantEntity);
    }

    @Test
    void testFindById() {
        when(repository.findById(123L)).thenReturn(Optional.of(restaurantEntity));
        when(mapper.map(restaurantEntity, Restaurant.class)).thenReturn(restaurant);

        Restaurant result = adapter.findById(123L);

        assertEquals(restaurant, result);
        verify(repository).findById(123L);
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(List.of(restaurantEntity));
        when(mapper.map(restaurantEntity, Restaurant.class)).thenReturn(restaurant);

        List<Restaurant> result = adapter.findAll();

        assertEquals(List.of(restaurant), result);
        verify(repository).findAll();
    }

    @Test
    void testFindByExample() {
        when(mapper.map(restaurant, RestaurantEntity.class)).thenReturn(restaurantEntity);
        when(repository.findAll(any())).thenReturn(List.of(restaurantEntity));
        when(mapper.map(restaurantEntity, Restaurant.class)).thenReturn(restaurant);

        List<Restaurant> result = adapter.findByExample(restaurant);

        assertEquals(List.of(restaurant), result);
        verify(repository).findAll(any());
    }

    @Test
    void saveRestaurant_success() {
        when(mapper.map(restaurant, RestaurantEntity.class)).thenReturn(restaurantEntity);
        when(repository.save(restaurantEntity)).thenReturn(restaurantEntity);
        when(mapper.map(restaurantEntity, Restaurant.class)).thenReturn(restaurant);

        Restaurant result = adapter.saveRestaurant(restaurant);

        assertEquals(restaurant, result);
        verify(mapper).map(restaurant, RestaurantEntity.class);
        verify(repository).save(restaurantEntity);
        assertEquals(owner.getUserId(), restaurantEntity.getOwnerId());
    }

    @Test
    void saveRestaurant_setsOwnerIdCorrectly() {
        when(mapper.map(restaurant, RestaurantEntity.class)).thenReturn(restaurantEntity);
        when(repository.save(restaurantEntity)).thenReturn(restaurantEntity);
        when(mapper.map(restaurantEntity, Restaurant.class)).thenReturn(restaurant);

        adapter.saveRestaurant(restaurant);

        assertEquals(1L, restaurantEntity.getOwnerId());
        verify(repository).save(restaurantEntity);
    }

    @Test
    void saveRestaurant_withNullOwner_throwsException() {
        Restaurant restaurantWithoutOwner = Restaurant.builder()
                .restaurantId(123L)
                .name("Test Restaurant")
                .owner(null)
                .build();

        when(mapper.map(restaurantWithoutOwner, RestaurantEntity.class)).thenReturn(restaurantEntity);

        assertThrows(NullPointerException.class, () -> adapter.saveRestaurant(restaurantWithoutOwner));
    }
}