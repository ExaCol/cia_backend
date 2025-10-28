package CIA.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import CIA.app.model.SOAT_FARE;

public interface SOAT_FARERepository extends JpaRepository<SOAT_FARE, Integer> {
    @Query("""
        SELECT s.price
        FROM SOAT_FARE s
        WHERE s.id = :id
    """)
    int findByPriceByCat(@Param("id") String id);
}