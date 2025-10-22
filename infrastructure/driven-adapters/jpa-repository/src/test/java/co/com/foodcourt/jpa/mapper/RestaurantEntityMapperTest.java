package co.com.foodcourt.jpa.mapper;


import co.com.foodcourt.jpa.entity.RestaurantEntity;
import co.com.foodcourt.model.restaurant.Restaurant;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantEntityMapperTest {

    private final RestaurantEntityMapper mapper = Mappers.getMapper(RestaurantEntityMapper.class);

    @Test
    void shouldMapRestaurantEntityToDomainSuccessfully() {
        RestaurantEntity entity = new RestaurantEntity();
        entity.setRestaurantId(1L);
        entity.setName("Juice");
        entity.setUrlLogo("https://cdn.com/sabor.png");

        Restaurant result = mapper.toDomain(entity);

        assertNotNull(result);
        assertEquals(1L, result.getRestaurantId());
        assertEquals("Juice", result.getName());
        assertEquals("https://cdn.com/sabor.png", result.getUrlLogo());
    }

    @Test
    void shouldMapRestaurantDomainToEntitySuccessfully() {
        Restaurant domain = new Restaurant();
        domain.setRestaurantId(2L);
        domain.setName("Turbo Burger");
        domain.setUrlLogo("https://cdn.com/turbo.png");

        RestaurantEntity result = mapper.toEntity(domain);

        assertNotNull(result);
        assertEquals(2L, result.getRestaurantId());
        assertEquals("Turbo Burger", result.getName());
        assertEquals("https://cdn.com/turbo.png", result.getUrlLogo());
    }
}
