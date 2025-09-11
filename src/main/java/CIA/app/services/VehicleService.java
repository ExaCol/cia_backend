package CIA.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CIA.app.repositories.VehicleRepository;

@Service
public class VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }
    
}
