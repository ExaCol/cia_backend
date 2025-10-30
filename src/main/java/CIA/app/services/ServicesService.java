package CIA.app.services;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import CIA.app.model.Usr;
import CIA.app.model.Vehicle;
import CIA.app.model.Services;
import CIA.app.repositories.CoursesDataRepository;
import CIA.app.repositories.SOAT_FARERepository;
import CIA.app.repositories.ServicesRepository;
import CIA.app.repositories.TECNO_FARERepository;
import CIA.app.repositories.UsrRepository;
import CIA.app.repositories.VehicleRepository;

@Service
public class ServicesService {
    @Autowired
    private ServicesRepository servicesRepository;
    @Autowired
    private UsrRepository usrRepository;
    @Autowired
    private SOAT_FARERepository soat_fareRepository;
    @Autowired
    private TECNO_FARERepository tecno_fareRepository;
    @Autowired
    private CoursesDataRepository courseDataRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    public ServicesService(ServicesRepository servicesRepository, UsrRepository usrRepository,
            SOAT_FARERepository soat_fareRepository, TECNO_FARERepository tecno_fareRepository,
            CoursesDataRepository courseDataRepository, VehicleRepository vehicleRepository) {
        this.servicesRepository = servicesRepository;
        this.usrRepository = usrRepository;
        this.soat_fareRepository = soat_fareRepository;
        this.tecno_fareRepository = tecno_fareRepository;
        this.courseDataRepository = courseDataRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public Services createServices(String email, Services service) {
        Usr user = usrRepository.findByEmail(email);
        if (user != null) {
            service.setUsr(user);
            String serviceType = service.getServiceType();
            serviceType = serviceType.toUpperCase();
            if(serviceType.equals("SOAT")){
                Vehicle vehicle = vehicleRepository.findByPlate(service.getPlate());
                int soatPrice = soat_fareRepository.findByPriceByCat(vehicle.getSoatRateType());
                service.setPrice(soatPrice);
                
            }else if(serviceType.equals("TECNO")){
                Vehicle vehicle = vehicleRepository.findByPlate(service.getPlate());
                int technoPrice = tecno_fareRepository.findPriceByYearAndType(vehicle.getModel(), vehicle.getType()).get(0);
                service.setPrice(technoPrice);

            }else if(serviceType.equals("COURSE")){
                String courseType = service.getCourseType().toUpperCase();
                int coursePrice = courseDataRepository.priceByCourseType(courseType);
                service.setPrice(coursePrice);
            }
            return servicesRepository.save(service);
        }
        return null;
    }

    public List<Services> getServicesByUser(String email) {
        Usr user = usrRepository.findByEmail(email);

        if (user != null) {
            return servicesRepository.getServicesByUser(user.getId());
        }
        return null;
    }

    public Services getSpecificServices(Integer serviceId) {
        Optional<Services> s = servicesRepository.findById(serviceId);
        return s.orElse(null);
    }

    public Services deleteEspecificServices(Integer serviceId) {
        Optional<Services> services = servicesRepository.findById(serviceId);
        if (services.isPresent()) {
            Services s = services.get();
            if(s.isPaid()){
                return null;
            }
            servicesRepository.delete(s);
            return s;
        }
        return null;
    }

    public boolean updateGraduated(Integer id) {
        Optional<Services> s = servicesRepository.findById(id);
        if (s.isPresent()) {
            if (s.get().isGraduated()) {
                return false;
            }
            s.get().setGraduated(true);
            servicesRepository.save(s.get());
            return true;
        }
        return false;
    }
}
