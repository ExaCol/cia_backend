package CIA.app.services;


//import java.util.Collections;
//import java.util.HashMap;
import java.util.List;
//import java.util.Map;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CIA.app.model.Usr;
//import CIA.app.model.Partner;
import CIA.app.model.Services;
import CIA.app.repositories.ServicesRepository;
import CIA.app.repositories.UsrRepository;


@Service
public class ServicesService {
    @Autowired
    private ServicesRepository servicesRepository;
    @Autowired
    private UsrRepository usrRepository;

    public ServicesService(ServicesRepository servicesRepository, UsrRepository usrRepository) {
        this.servicesRepository = servicesRepository;
        this.usrRepository = usrRepository;
    }
    
    public Services createServices(String email, Services service) {
        Usr user = usrRepository.findByEmail(email);
        if (user != null) {
            service.setUsr(user);

            return servicesRepository.save(service);
        }
        return null;
    }

    public List<Services> getServicesByUser(String email){
        Usr user = usrRepository.findByEmail(email);

        if (user != null) {
            return servicesRepository.getServicesByUser(user.getId());
        }
        return null;
    }

    public Services getSpecificServices(Integer serviceId){
        Optional<Services> s = servicesRepository.findById(serviceId);
        return s.orElse(null);
    }

    public Services deleteEspecificServices(Services service){
        Services s = getSpecificServices(service.getId());
        if(s != null){
            servicesRepository.delete(s);
            return s;
        }
        return null;
    }

    // public Map<Integer, Partner> getPartnersByTypeServicesNR(String email, String type){
    //     Usr user = usrService.findByEmail(email);
    //     if (user != null) {
    //         List<Services> sR = servicesRepository.getServicesByType(type);
    //         if(sR==null || sR.isEmpty()) return Collections.emptyMap();

    //         Map<Integer, Partner> partnersMapWR = new HashMap<>();

    //         for(Services s : sR){
    //             if (s.getPartner() == null) continue;

    //             for (Partner p : s.getPartner()) {
    //                 if (p == null || p.getId() == null) continue;

    //                 partnersMapWR.putIfAbsent(p.getId(), p);
    //             }
    //         }

    //         return partnersMapWR;
    //     }
    //     return Collections.emptyMap();
    // }


    
}
