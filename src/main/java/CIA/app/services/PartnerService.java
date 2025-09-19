package CIA.app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import CIA.app.model.Partner;
//import CIA.app.model.Payments;
import CIA.app.model.Usr;
import CIA.app.model.Services;
import CIA.app.repositories.PartnerRepository;

@Service
public class PartnerService {
    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private UsrService usrService;
    @Autowired
    private ServicesService servicesService;

    public PartnerService(PartnerRepository partnerRepository, UsrService usrService, ServicesService servicesService) {
        this.partnerRepository = partnerRepository;
        this.usrService = usrService;
        this.servicesService = servicesService;
    }

    public Partner createPartner(String email, Partner partner, Services service){
        Usr user = usrService.findByEmail(email);
        if (user != null) {
            partner.setService(service);
            return partnerRepository.save(partner);
        }
        return null;
    }

    public Partner getSpecificPartner(Partner partner){
        Optional<Partner> p = partnerRepository.findById(partner.getId());
        return p.orElse(null);
    }

    public List<Partner> getPartnersByServices(Services services){
        if (servicesService.getSpecificServices(services) != null) {
            return partnerRepository.getPartnersByServices(services.getId());
        }
        return null;
    }

    public Partner deleteSpecificPartner(Partner partner) {
        Partner p = getSpecificPartner(partner);
        if (p != null) {
            partnerRepository.delete(p);
            return p;
        }
        return null;
    }

    // public Partner update(String currentEmail, Partner partner) {
    //     Usr user = usrService.findByEmail(currentEmail);
    //     if(user != null){
    //         //Usr emailP = usrService.findByEmail(partner.getService().getUsr().getEmail());
    //         if(emailP == null || user.equals(emailP)){
                
    //             //return usrRepository.save(user);
    //         }
    //         return null;
    //     }
    //     return null;
    // }
}
