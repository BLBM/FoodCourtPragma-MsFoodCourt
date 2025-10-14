package co.com.foodcourt.config;

import co.com.foodcourt.model.plate.gateways.DishRepository;
import co.com.foodcourt.model.restaurant.gateways.RestaurantRepository;
import co.com.foodcourt.model.user.gateways.UserRepository;
import co.com.foodcourt.usecase.createrestaurant.CreateRestaurantUseCase;
import co.com.foodcourt.usecase.dish.DishUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public MyUseCase myUseCase() {
            return new MyUseCase();
        }

        @Bean
        public UserRepository userRepository() { return Mockito.mock(UserRepository.class);}

        @Bean
        public RestaurantRepository restaurantRepository(){return  Mockito.mock(RestaurantRepository.class);}

        @Bean
        public CreateRestaurantUseCase createRestaurantUseCase(){return  Mockito.mock(CreateRestaurantUseCase.class);}

        @Bean
        public DishUseCase dishUseCase(){return  Mockito.mock(DishUseCase.class);}

        @Bean
        public DishRepository dishRepository(){return  Mockito.mock(DishRepository.class);}
    }

    static class MyUseCase {
        public String execute() {
            return "MyUseCase Test";
        }
    }
}