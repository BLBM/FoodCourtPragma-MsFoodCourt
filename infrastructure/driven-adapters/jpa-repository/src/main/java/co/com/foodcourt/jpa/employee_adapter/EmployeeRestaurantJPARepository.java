package co.com.foodcourt.jpa.employee_adapter;


import co.com.foodcourt.jpa.entity.EmployeeRestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRestaurantJPARepository extends JpaRepository<EmployeeRestaurantEntity, Long>{

    @Query(EmployeeQuerys.FIND_RESTAURANT_BY_EMPLOYEE_ID)
    Long findRestaurantIdByEmployeeId(@Param("employeeId") Long employeeId);
}
