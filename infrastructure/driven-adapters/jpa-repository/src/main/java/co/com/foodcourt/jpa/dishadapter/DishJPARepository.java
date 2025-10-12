package co.com.foodcourt.jpa.dishadapter;

import co.com.foodcourt.jpa.entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DishJPARepository extends JpaRepository<DishEntity, Long>{
}
