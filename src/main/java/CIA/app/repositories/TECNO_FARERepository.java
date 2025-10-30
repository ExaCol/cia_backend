package CIA.app.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import CIA.app.model.TECNO_FARE;

public interface TECNO_FARERepository extends JpaRepository<TECNO_FARE, Integer> {

    boolean existsByTypeAndStartYearAndEndYear(String type, Integer startYear, Integer endYear);

    Optional<TECNO_FARE> findByTypeAndStartYearAndEndYear(String type, Integer startYear, Integer endYear);

    @Query("""
            SELECT t.price
            FROM TECNO_FARE t
            WHERE :anio BETWEEN t.startYear AND COALESCE(t.endYear, 9999)
              AND UPPER(t.type) = UPPER(:type)
            ORDER BY t.startYear DESC
            """)
    List<Integer> findPriceByYearAndType(
            @Param("anio") int anio,
            @Param("type") String type);
}
