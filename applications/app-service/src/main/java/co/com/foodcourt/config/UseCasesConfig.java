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
import co.com.foodcourt.usecase.traceability_recorder.TraceabilityRecorderUseCase;
import co.com.foodcourt.usecase.update_order.UpdateOrderUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.foodcourt.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

    @Bean
    public CreateRestaurantUseCase createRestaurantUseCase(RestaurantRepository restaurantRepository, UserRepository userRepository){
        return new CreateRestaurantUseCase(restaurantRepository,userRepository);
    }

    @Bean
    public DishUseCase dishUseCase(RestaurantRepository restaurantRepository, DishRepository dishRepository){
        return new DishUseCase(dishRepository,restaurantRepository);
    }

    @Bean
    public CreateOrderUseCase createOrderUseCase(OrderRepository orderRepository,
                                           RestaurantRepository restaurantRepository,
                                           DishRepository dishRepository,
                                           EmployeeRestaurantRepository employeeRestaurantRepository,
                                           TraceabilityRecorderUseCase traceabilityRecorderUseCase){
        return new CreateOrderUseCase(orderRepository,restaurantRepository,dishRepository,employeeRestaurantRepository,traceabilityRecorderUseCase);
    }

    @Bean TraceabilityRecorderUseCase traceabilityRecorderUseCase(TraceabilityService traceabilityService){
        return new TraceabilityRecorderUseCase(traceabilityService);
    }

    @Bean
    public UpdateOrderUseCase updateOrderUseCase(OrderRepository orderRepository,
                                                 TraceabilityRecorderUseCase traceabilityRecorderUseCase,
                                                 NotificationService notificationService){
        return new UpdateOrderUseCase(orderRepository,traceabilityRecorderUseCase, notificationService);
    }



}
