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
    private final PartnerRepository partnerRepository;
    @Autowired
    private final UsrService usrService;
    @Autowired
    private final ServicesService servicesService;

    public PartnerService(PartnerRepository partnerRepository, UsrService usrService, ServicesService servicesService) {
        this.partnerRepository = partnerRepository;
        this.usrService = usrService;
        this.servicesService = servicesService;
    }

    public Partner createPartner(String email, Partner partner) {
        Usr user = usrService.findByEmail(email);
        if (user != null) {

            /*
             * Integer partnerId = partner.getService().getId();
             * if (partnerId.equals(null)) {
             * throw new
             * IllegalArgumentException("El partner debe tener un servicio asociado");
             * }
             * 
             * Services existing = servicesService.getSpecificServices(partnerId);
             * if (!partnerId.equals(existing.getId())){
             * throw new IllegalStateException("Ingrese un servicio válido");
             * }
             * partner.setService(existing);
             */

            return partnerRepository.save(partner);
        }
        return null;
    }

    public List<Partner> getAllPartners() {
        return partnerRepository.findAll();
    }

    public Partner getSpecificPartner(Integer partnerId) {
        Optional<Partner> p = partnerRepository.findById(partnerId);
        return p.orElse(null);
    }

    public List<Partner> getPartnerByService(String type) {
        if (type.equals("SOAT")) {
            return partnerRepository.getPartnersBySoat();
        } else if (type.equals("techno")) {
            return partnerRepository.getPartnersByTechno();
        }
        return partnerRepository.getCIA();
    }

    // public List<Partner> getPartnersByServices(Integer serviceId){
    // if (servicesService.getSpecificServices(serviceId) != null) {
    // return partnerRepository.getPartnersByServices(serviceId);
    // }
    // return null;
    // }

    public Partner deleteSpecificPartner(Integer partnerId) {
        {
            Partner p = partnerRepository.findById(partnerId).get();
            if (p != null) {
                partnerRepository.delete(p);
                return p;
            }
        }
        return null;
    }

    public Partner update(String email, Partner partner) {
        Usr user = usrService.findByEmail(email);
        if (user != null) {
            Partner sPartner = getSpecificPartner(partner.getId());
            if (sPartner.getId().equals(partner.getId())) {
                sPartner.setName(partner.getName());
                sPartner.setLon(partner.getLon());
                sPartner.setLat(partner.getLat());
                sPartner.setSoat(partner.isSoat());
                sPartner.setTechno(partner.isTechno());
                return partnerRepository.save(sPartner);
            }
        }
        return null;
    }

}
