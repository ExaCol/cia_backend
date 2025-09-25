package CIA.app.repositories;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import CIA.app.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Vehicle findByPlate(String plate);
    Vehicle findByUsr_EmailAndPlate(String email, String plate);

    @Query("""
        SELECT v
        FROM Vehicle v
        WHERE
          (v.soatExpiration IS NOT NULL
             AND v.soatExpiration >= CURRENT_DATE
             AND v.soatExpiration <= :limitDate)
          OR
          (v.technoExpiration IS NOT NULL
             AND v.technoExpiration >= CURRENT_DATE
             AND v.technoExpiration <= :limitDate)
    """)
    List<Vehicle> findVehiclesExpiringBy(@Param("limitDate") Date limitDate);
}
