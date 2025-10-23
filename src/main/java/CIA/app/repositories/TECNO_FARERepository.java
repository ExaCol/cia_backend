package CIA.app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import CIA.app.model.SOAT_FARE;
import CIA.app.model.TECNO_FARE;

public interface TECNO_FARERepository extends JpaRepository<TECNO_FARE, Integer>{
    
   boolean existsByTypeAndStartYearAndEndYear(String type, Integer startYear, Integer endYear);
    Optional<TECNO_FARE> findByTypeAndStartYearAndEndYear(String type, Integer startYear, Integer endYear);
}