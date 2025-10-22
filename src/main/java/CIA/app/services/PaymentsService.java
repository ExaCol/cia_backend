package CIA.app.services;

import java.time.LocalDate;

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
    private ServicesRepository servicesRepository;

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
                throw new IllegalStateException("Ingrese servicios válidos");
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

    public List<Payments> getPaymentHistoryByUserId(String email){
        Usr usr = usrService.findByEmail(email);
        if(usr != null){
            return paymentsRepository.findAllByUserServicesOrderByReleaseDateDesc(usr.getId());
        }
        return null;
    }


    public int getPaymentNumber(LocalDate startDate, LocalDate endDate){
        return paymentsRepository.findPaymentsNumBetweenDates(startDate, endDate);

    }

    public Double earningsByCat(String type){
        return paymentsRepository.earningsByCat(type);
    }

    public Double getNetWorth(){
        return paymentsRepository.findTotalPaymentsAmount();
    }

    public Double savedUsrMoney(){
        return paymentsRepository.savedUsrMoney("TICKET");
    }

    public Integer graduatedNum(){
        return paymentsRepository.graduatedUsr();
    }
}

