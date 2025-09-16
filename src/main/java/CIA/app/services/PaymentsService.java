package CIA.app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import CIA.app.model.Usr;
import CIA.app.model.Payments;
import CIA.app.model.Services;
import CIA.app.repositories.PaymentsRepository;

public class PaymentsService {

    @Autowired
    private PaymentsRepository paymentsRepository;
    @Autowired
    private UsrService usrService;

    public PaymentsService(PaymentsRepository paymentsRepository) {
        this.paymentsRepository = paymentsRepository;
    }
    
    public Payments createPayments(String email, Payments payment, List<Services> services) {
        Usr user = usrService.findByEmail(email);
        if (user != null) {
            payment.setServices(services);
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

    public Payments getSpecificPayments(Payments payment){
        Optional<Payments> p = paymentsRepository.findById(payment.getId());
        return p.orElse(null);
    }

    public Payments deleteEspecificPayments(Payments payment){

        Payments p = getSpecificPayments(payment);
        if(p != null){
            paymentsRepository.delete(p);
            return p;
        }

        return null;
    }


}
