package CIA.app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CIA.app.model.Usr;
import CIA.app.model.Services;
import CIA.app.repositories.ServicesRepository;

@Service
public class ServicesService {
    @Autowired
    private ServicesRepository servicesRepository;
    @Autowired
    private UsrService usrService;

    public ServicesService(ServicesRepository servicesRepository) {
        this.servicesRepository = servicesRepository;
    }
    
    public Services createServices(String email, Services service) {
        Usr user = usrService.findByEmail(email);
        if (user != null) {
            service.setUsr(user);

            return servicesRepository.save(service);
        }
        return null;
    }

    public List<Services> getServicesByUser(String email){
        Usr user = usrService.findByEmail(email);
        if (user != null) {
            return servicesRepository.getServicesByUser(user.getId());
        }
        return null;
    }

    public Services getSpecificServices(Services service){
        Optional<Services> s = servicesRepository.findById(service.getId());
        return s.orElse(null);
    }

    public Services deleteEspecificServices(Services service){
        Services s = getSpecificServices(service);
        if(s != null){
            servicesRepository.delete(s);
            return s;
        }

        return null;
    }

    
}
