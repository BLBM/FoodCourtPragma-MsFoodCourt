package co.com.foodcourt.config;


import co.com.foodcourt.model.employee_restaurant.gateways.EmployeeRestaurantRepository;
import co.com.foodcourt.model.order.gateways.NotificationService;
import co.com.foodcourt.model.order.gateways.OrderRepository;
import co.com.foodcourt.model.order.gateways.TraceabilityService;
import co.com.foodcourt.model.plate.gateways.DishRepository;
import co.com.foodcourt.model.restaurant.gateways.RestaurantRepository;
import co.com.foodcourt.model.user.gateways.UserRepository;
import co.com.foodcourt.usecase.createrestaurant.CreateRestaurantUseCase;
import co.com.foodcourt.usecase.dish.DishUseCase;
import co.com.foodcourt.usecase.create_order.CreateOrderUseCase;
import co.com.foodcourt.usecase.employee_restaurant.EmployeeRestaurantUseCase;
import co.com.foodcourt.usecase.traceability_recorder.TraceabilityRecorderUseCase;
import co.com.foodcourt.usecase.update_order.UpdateOrderUseCase;
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

        @Bean
        public CreateOrderUseCase orderUseCase(){return  Mockito.mock(CreateOrderUseCase.class);}

        @Bean
        public OrderRepository orderRepository(){return  Mockito.mock(OrderRepository.class);}

        @Bean
        public NotificationService notificationService(){return  Mockito.mock(NotificationService.class);}

        @Bean
        public TraceabilityService traceabilityService(){return  Mockito.mock(TraceabilityService.class);}

        @Bean
        public TraceabilityRecorderUseCase traceabilityRecorderUseCase(){return Mockito.mock(TraceabilityRecorderUseCase.class);}

        @Bean
        public UpdateOrderUseCase updateOrderUseCase(){return  Mockito.mock(UpdateOrderUseCase.class);}

        @Bean
        public EmployeeRestaurantRepository employeeRestaurantRepository(){ return  Mockito.mock(EmployeeRestaurantRepository.class);}

        @Bean
        public EmployeeRestaurantUseCase employeeRestaurantUseCase(){return Mockito.mock(EmployeeRestaurantUseCase.class);}

    }

    static class MyUseCase {
        public String execute() {
            return "MyUseCase Test";
        }
    }
}