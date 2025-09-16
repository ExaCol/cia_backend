package CIA.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CIA.app.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Vehicle findByPlate(String plate);
    Vehicle findByUsr_EmailAndPlate(String email, String plate);
}
