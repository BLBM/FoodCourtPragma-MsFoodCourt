package co.com.foodcourt.usecase.employee_restaurant;

import co.com.foodcourt.model.employee_restaurant.EmployeeRestaurant;
import co.com.foodcourt.model.employee_restaurant.gateways.EmployeeRestaurantRepository;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.restaurant.gateways.RestaurantRepository;
import co.com.foodcourt.model.user.User;
import co.com.foodcourt.model.user.gateways.UserRepository;
import co.com.foodcourt.usecase.util.ValidateUser;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class EmployeeRestaurantUseCase {

    private final EmployeeRestaurantRepository repository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public void assign(Long employeeId, Long restaurantId, Long ownerId){

        Restaurant restaurant = restaurantRepository.getRestaurantById(restaurantId);

        ValidateUser.validateOwner(ownerId,restaurant.getOwner().getUserId());

        User employee = userRepository.getUserById(employeeId);

        ValidateUser.validateRoleEmployee(employee.getRole());

        repository.saveEmployeeRestaurant(EmployeeRestaurant.builder().employeeId(employeeId).restaurantId(restaurantId).build());

    }

}
