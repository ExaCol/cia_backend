package CIA.app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CIA.app.model.Usr;
import CIA.app.model.Payments;
import CIA.app.model.Services;
import CIA.app.repositories.PaymentsRepository;
import CIA.app.repositories.ServicesRepository;

@Service
public class PaymentsService {

    @Autowired
    private PaymentsRepository paymentsRepository;
    @Autowired
    private UsrService usrService;
    @Autowired 
    ServicesRepository servicesRepository;

    public PaymentsService(PaymentsRepository paymentsRepository, UsrService usrService, ServicesRepository servicesRepository) {
        this.paymentsRepository = paymentsRepository;
        this.usrService = usrService;
        this.servicesRepository = servicesRepository;
    }
    
    public Payments createPayments(String email, Payments payment) {
        Usr user = usrService.findByEmail(email);
        if (user != null) {
            
            List<Integer> ids = payment.getServices().stream().map(Services::getId).toList();
            if (ids.isEmpty()) {
                throw new IllegalArgumentException("Debe enviar un pago con servicio/s asociado/s");
            }

            List<Services> existing = servicesRepository.findAllById(ids);
            if (existing.size() != ids.size()) {
                throw new IllegalArgumentException("Ingrese servicios válidos");
            }

            for(Services s: existing){
                s.setPayment(payment);
            }

            payment.setServices(existing);
            return paymentsRepository.save(payment);
        }
        return null;
    }

    public List<Payments> getPaymentsByUser(String email){
        Usr user = usrService.findByEmail(email);
        if (user != null) {
            return paymentsRepository.getPaymentsByUser(user.getId());
        }
        return null;
    }

    public Payments getSpecificPayments(Integer paymentId){
        Optional<Payments> p = paymentsRepository.findById(paymentId);
        return p.orElse(null);
    }

    public Payments deleteEspecificPayments(Payments payment){

        Payments p = getSpecificPayments(payment.getId());
        if(p != null){
            paymentsRepository.delete(p);
            return p;
        }

        return null;
    }


}
