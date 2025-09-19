package CIA.app.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CIA.app.model.Usr;
import CIA.app.model.Vehicle;
import CIA.app.repositories.VehicleRepository;

@Service
public class VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private UsrService usrService;
    
    public VehicleService(VehicleRepository vehicleRepository, UsrService usrService) {
        this.vehicleRepository = vehicleRepository;
        this.usrService = usrService;
    }

    public Vehicle saveVehicle(String email, Vehicle vehicle) {
        Vehicle existingVehicle = vehicleRepository.findByPlate(vehicle.getPlate());
        if (existingVehicle != null) {
            return null;
        }
        Usr user = usrService.findByEmail(email);
        if (user != null) {
            vehicle.setUsr(user);
            vehicle.setPlate(vehicle.getPlate().toUpperCase());
            return vehicleRepository.save(vehicle);
        }
        return null;
    }

    public Vehicle getByPlate(String plate) {
        return vehicleRepository.findByPlate(plate);
    }

    public List<Vehicle> getVehicles(String email) {
        return usrService.findByEmail(email).getVehicles();
    }

    public Vehicle deleteByPlate(String email, String plate) {
        Vehicle vehicle = vehicleRepository.findByUsr_EmailAndPlate(email, plate);
        if (vehicle != null) {
            vehicleRepository.delete(vehicle);
            return vehicle;
        }
        return null;
    }
}
