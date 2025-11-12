package CIA.app.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test; 
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import CIA.app.model.Partner;
import CIA.app.model.Services;
import CIA.app.model.Usr;
import CIA.app.repositories.PartnerRepository;
import CIA.app.repositories.UsrRepository;

@ExtendWith(MockitoExtension.class)
public class PartnerTest {

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private UsrRepository usrRepository;

    @Mock
    private UsrService usrService;

    @InjectMocks
    private PartnerService partnerService;

    private Partner makePartner(Integer id, String name, double lat, double lon, boolean soat, boolean techno, List<Services> service){
        Partner p = new Partner();
        p.setId(id);
        p.setName(name);
        p.setLat(lat);
        p.setLon(lon);
        p.setSoat(soat);
        p.setTechno(techno);
        p.setService(service);
        return p;
    }

    private Services makeService(Integer id, String name, int price, String serviceType, String courseType, String plate, boolean paid, boolean graduated){
        Services s = new Services();
        s.setId(id);
        s.setName(name);
        s.setPrice(price);
        s.setServiceType(serviceType);
        s.setCourseType(courseType);
        s.setPlate(plate);
        s.setPaid(paid);
        s.setGraduated(graduated);
        return s;
    }

    private Usr makeUsr(int id, String email){
        Usr u = new Usr();
        u.setId(id);
        u.setEmail(email);
        return u;
    }

    @Test
    public void createPartner_ok(){
        String email = "testEmail@gmail.com";
        Usr user = makeUsr(1, email);
        List<Services> services = List.of(
            makeService(1, "Service 1", 100000, "SOAT", null, "ABC123", false, false),
            makeService(2, "Service 2", 120000, "TECNO", null, "ABC124", false, false),
            makeService(3, "Service 3", 100000, "SOAT", null, "ABC125", false, false)
        );
        Partner mP = makePartner(1, "Partner1", 4.654, -74.054, true, true, services);
        when(usrService.findByEmail(email)).thenReturn(user);
        when(partnerRepository.save(mP)).thenReturn(mP);

        Partner createPartner = partnerService.createPartner(email, mP);

        assertEquals(email, user.getEmail());
        assertEquals(mP.getId(), createPartner.getId());
        assertEquals(services.size(), createPartner.getService().size());

    }

    @Test
    public void getAllPartners_ok(){
        List<Services> services1 = List.of(
            makeService(1, "Service 1", 100000, "SOAT", null, "ABC123", false, false),
            makeService(2, "Service 2", 120000, "TECNO", null, "ABC124", false, false),
            makeService(3, "Service 3", 100000, "SOAT", null, "ABC125", false, false)
        );
        Partner mP1 = makePartner(1, "Partner1", 4.654, -74.054, true, true, services1);

        List<Services> services2 = List.of(
            makeService(4, "Service 4", 100000, "SOAT", null, "ABC126", false, false),
            makeService(5, "Service 5", 120000, "TECNO", null, "ABC127", false, false),
            makeService(6, "Service 6", 100000, "SOAT", null, "ABC128", false, false)
        );
        Partner mP2 = makePartner(2, "Partner2", 4.654, -74.054, true, true, services2);

        when(partnerRepository.findAll()).thenReturn(List.of(mP1, mP2));

        List<Partner> getAllPartners = partnerService.getAllPartners();

        assertEquals(2, getAllPartners.size());
        assertEquals(mP1.getId(), getAllPartners.get(0).getId());
        assertEquals(mP2.getId(), getAllPartners.get(1).getId());
        
    }

    @Test
    public void getSpecificPartner_ok(){
        List<Services> services1 = List.of(
            makeService(1, "Service 1", 100000, "SOAT", null, "ABC123", false, false),
            makeService(2, "Service 2", 120000, "TECNO", null, "ABC124", false, false),
            makeService(3, "Service 3", 100000, "SOAT", null, "ABC125", false, false)
        );
        Partner mP1 = makePartner(1, "Partner1", 4.654, -74.054, true, true, services1);

        when(partnerRepository.findById(mP1.getId())).thenReturn(java.util.Optional.of(mP1));

        Partner getSpecificPartner = partnerService.getSpecificPartner(1);

        assertEquals(mP1, getSpecificPartner);
        assertEquals(mP1.getId(), getSpecificPartner.getId());

    }

    @Test
    public void getPartnerByService_ok(){
        List<Services> services1 = List.of(
            makeService(1, "Service 1", 100000, "SOAT", null, "ABC123", false, false),
            makeService(2, "Service 2", 120000, "SOAT", null, "ABC124", false, false),
            makeService(3, "Service 3", 100000, "SOAT", null, "ABC125", false, false)
        );
        Partner mP1 = makePartner(1, "Partner1", 4.654, -74.054, true, true, services1);

        // List<Services> services2 = List.of(
        //     makeService(4, "Service 4", 100000, "SOAT", null, "ABC126", false, false),
        //     makeService(5, "Service 5", 120000, "TECNO", null, "ABC127", false, false),
        //     makeService(6, "Service 6", 100000, "SOAT", null, "ABC128", false, false)
        // );
        // Partner mP2 = makePartner(2, "Partner2", 4.654, -74.054, false, false, services2);

        when(partnerRepository.getPartnersBySoat()).thenReturn(List.of(mP1));

        List<Partner> getPartnerByService = partnerService.getPartnerByService("SOAT");

        assertEquals(List.of(mP1), getPartnerByService);
        assertEquals(1, getPartnerByService.size());
        assertEquals(mP1.getId(), getPartnerByService.getFirst().getId());
        assertEquals(mP1.getService().getFirst().getServiceType(), getPartnerByService.getFirst().getService().getFirst().getServiceType());
    }

    @Test
    public void deleteSpecificPartner_ok(){
        List<Services> services1 = List.of(
            makeService(1, "Service 1", 100000, "SOAT", null, "ABC123", false, false),
            makeService(2, "Service 2", 120000, "SOAT", null, "ABC124", false, false),
            makeService(3, "Service 3", 100000, "SOAT", null, "ABC125", false, false)
        );
        Partner mP1 = makePartner(1, "Partner1", 4.654, -74.054, true, true, services1);

        when(partnerRepository.findById(mP1.getId())).thenReturn(java.util.Optional.of(mP1));
        //A diferencia de un save, un .delete es void y por eso cambia esta lógica
        doNothing().when(partnerRepository).delete(mP1);

        Partner deleteSpecificPartner = partnerService.deleteSpecificPartner(1);

        assertEquals(mP1.getId(), deleteSpecificPartner.getId());
        verify(partnerRepository, times(1)).delete(mP1);
        verifyNoMoreInteractions(partnerRepository);

    }

    @Test
    public void updatePartner_ok(){
        String email = "testEmail@gmail.com";
        Usr user = makeUsr(1, email);
        List<Services> services = List.of(
            makeService(1, "Service 1", 100000, "SOAT", null, "ABC123", false, false),
            makeService(2, "Service 2", 120000, "TECNO", null, "ABC124", false, false),
            makeService(3, "Service 3", 100000, "SOAT", null, "ABC125", false, false)
        );
        Partner mP1 = makePartner(1, "Partner1", 4.654, -74.054, true, true, services);
        
        List<Services> services2 = List.of(
            makeService(1, "Service 4", 100000, "SOAT", null, "ABC123", false, false),
            makeService(2, "Service 5", 120000, "TECNO", null, "ABC124", false, false),
            makeService(3, "Service 6", 100000, "SOAT", null, "ABC125", false, false)
        );
        Partner mP2 = makePartner(1, "Partner2", 4.6541, -74.0541, true, true, services2);
        
        when(usrService.findByEmail(email)).thenReturn(user);
        when(partnerRepository.findById(mP1.getId())).thenReturn(java.util.Optional.of(mP1));
        when(partnerRepository.save(mP1)).thenReturn(mP2);

        Partner update = partnerService.update(email, mP2);

        assertEquals(mP1.getName(), update.getName());
        assertEquals(mP1.getId(), update.getId());
        assertEquals(mP1.getLon(), update.getLon());
        assertEquals(mP1.getLat(), update.getLat());
        assertEquals(mP1.getService(), update.getService());

    }
    
}
