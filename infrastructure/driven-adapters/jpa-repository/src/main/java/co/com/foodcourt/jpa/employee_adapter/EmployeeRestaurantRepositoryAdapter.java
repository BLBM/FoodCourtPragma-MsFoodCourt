package co.com.foodcourt.jpa.employee_adapter;


import co.com.foodcourt.jpa.common.LogConstants;
import co.com.foodcourt.jpa.entity.EmployeeRestaurantEntity;
import co.com.foodcourt.jpa.entity.RestaurantEntity;
import co.com.foodcourt.jpa.helper.AdapterOperations;
import co.com.foodcourt.model.employee_restaurant.EmployeeRestaurant;
import co.com.foodcourt.model.employee_restaurant.gateways.EmployeeRestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class EmployeeRestaurantRepositoryAdapter extends AdapterOperations
        <EmployeeRestaurant, EmployeeRestaurantEntity, Long, EmployeeRestaurantJPARepository>
        implements EmployeeRestaurantRepository {

    public EmployeeRestaurantRepositoryAdapter(EmployeeRestaurantJPARepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, EmployeeRestaurant.class));
    }

    @Override
    public void saveEmployeeRestaurant(EmployeeRestaurant employeeRestaurant) {

        log.info(LogConstants.SAVE_EMPLOYEE_RESTAURANT.getMessage(),employeeRestaurant.getEmployeeId());

        EmployeeRestaurantEntity employeeRestaurantEntity = super.mapper.map(employeeRestaurant, EmployeeRestaurantEntity.class);

        employeeRestaurantEntity.setRestaurant(
                RestaurantEntity.builder()
                        .restaurantId(employeeRestaurant.getRestaurantId())
                        .build());

        EmployeeRestaurantEntity employeeRestaurantSaved = repository.save(employeeRestaurantEntity);

        log.info(LogConstants.EMPLOYEE_RESTAURANT_SAVED.getMessage(),employeeRestaurantSaved.getId());
    }

}





