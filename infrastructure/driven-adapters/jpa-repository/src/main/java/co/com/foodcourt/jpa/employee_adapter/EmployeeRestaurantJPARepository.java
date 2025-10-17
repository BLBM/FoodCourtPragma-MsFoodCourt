package co.com.foodcourt.jpa.employee_adapter;


import co.com.foodcourt.jpa.entity.EmployeeRestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRestaurantJPARepository extends JpaRepository<EmployeeRestaurantEntity, Long>{

}
