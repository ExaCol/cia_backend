package CIA.app.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import CIA.app.model.SOAT_FARE;
import CIA.app.model.Services;
import CIA.app.model.Usr;
import CIA.app.model.Vehicle;
import CIA.app.repositories.CoursesDataRepository;
import CIA.app.repositories.SOAT_FARERepository;
import CIA.app.repositories.ServicesRepository;
import CIA.app.repositories.TECNO_FARERepository;
import CIA.app.repositories.UsrRepository;
import CIA.app.repositories.VehicleRepository;

  

@ExtendWith(MockitoExtension.class)
public class ServicesServiceTest {
    
    @Mock
    private ServicesRepository servicesRepository;

    @Mock
    private UsrRepository usrRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock 
    SOAT_FARERepository soat_fareRepository;

    @Mock 
    TECNO_FARERepository tecno_fareRepository;

    @Mock 
    CoursesDataRepository courseDataRepository;

    @InjectMocks
    private UsrService usrService;

    @InjectMocks
    private ServicesService servicesService;


    private static Usr usr(int id, String email){
        Usr u = new Usr();
        u.setId(id);
        u.setEmail(email);
        return u;
    }

    private Services service(Integer id, String serviceType, String plate, int price){
        Services s = new Services();
        s.setId(id);
        s.setServiceType(serviceType);
        s.setPlate(plate);
        s.setPrice(price);
        return s;
    }

    private static Vehicle vehicle(String plate, String soatRateType, Integer model, String type){
        Vehicle v = new Vehicle();
        v.setPlate(plate);
        v.setSoatRateType(soatRateType);
        v.setModel(model);
        v.setType(type);
        return v;
    }

    @Test
    void createServices_ok(){
        Integer id = 1;
        String email = "nico@exa.co";
        Usr user = usr(id, email);
        Vehicle veh = vehicle("ABC123", "BASIC", 2018, "SEDAN");
        user.setVehicles(List.of(veh));
        when(usrRepository.findByEmail(email)).thenReturn(user);
        when(vehicleRepository.findByPlate("ABC123")).thenReturn(veh);
        Services service = service(id, "SOAT", "ABC123", 300000);
        when(usrService.findByEmail(user.getEmail())).thenReturn(user);
        when(servicesRepository.save(service)).thenReturn(service);

        Services createdService = servicesService.createServices(email, service);

        assertEquals(service.getId(), createdService.getId());
        assertEquals(service.getServiceType(), createdService.getServiceType());
        assertEquals(service.getPlate(), createdService.getPlate());
        assertEquals(service.getPrice(), createdService.getPrice());

    }

    @Test
    void getServicesByUser_ok(){
        Integer id = 1;
        String email = "nico@exa.co";
        Usr user = usr(id, email);

        when(usrRepository.findByEmail(email)).thenReturn(user);
        when(servicesRepository.getServicesByUser(id)).thenReturn(List.of(
            service(1, "SOAT", "ABC123", 300000),
            service(2, "TECNO", "XYZ789", 200000)
        ));

        List<Services> services = servicesService.getServicesByUser(email);

        assertEquals(2, services.size());
        assertEquals("SOAT", services.get(0).getServiceType());
        assertEquals("TECNO", services.get(1).getServiceType());
    }

    @Test
    void getSpecificServices_ok(){
        Integer serviceId = 1;
        Services service = service(serviceId, "SOAT", "ABC123", 300000);
        when(servicesRepository.findById(serviceId)).thenReturn(java.util.Optional.of(service));

        Services foundService = servicesService.getSpecificServices(serviceId);

        assertEquals(service.getId(), foundService.getId());
        assertEquals(service.getServiceType(), foundService.getServiceType());
        assertEquals(service.getPlate(), foundService.getPlate());
        assertEquals(service.getPrice(), foundService.getPrice());
    }

    @Test
    void deleteEspecificServices_ok(){
        Integer serviceId = 1;
        Services service = service(serviceId, "SOAT", "ABC123", 300000);
        service.setPaid(false);
        when(servicesRepository.findById(serviceId)).thenReturn(java.util.Optional.of(service));

        Services deletedService = servicesService.deleteEspecificServices(serviceId);

        assertEquals(service.getId(), deletedService.getId());
        assertEquals(service.getServiceType(), deletedService.getServiceType());
        assertEquals(service.getPlate(), deletedService.getPlate());
        assertEquals(service.getPrice(), deletedService.getPrice());
    }

    @Test
    void updateGraduated_ok(){
        Integer serviceId = 1;
        Services service = service(serviceId, "COURSE", null, 500000);
        service.setGraduated(false);
        when(servicesRepository.findById(serviceId)).thenReturn(java.util.Optional.of(service));

        boolean result = servicesService.updateGraduated(serviceId);

        assertEquals(true, result);
    }
}

