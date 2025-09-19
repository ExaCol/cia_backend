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

@Service
public class PaymentsService {

    @Autowired
    private PaymentsRepository paymentsRepository;
    @Autowired
    private UsrService usrService;

    
    
    public PaymentsService(PaymentsRepository paymentsRepository, UsrService usrService) {
        this.paymentsRepository = paymentsRepository;
        this.usrService = usrService;
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

    public List<Payments> getPaymentHistoryByUserId(String email){
        Usr usr = usrService.findByEmail(email);
        if(usr != null){
            return paymentsRepository.findAllByUserServicesOrderByReleaseDateDesc(usr.getId());
        }
        return List.of();
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
        return paymentsRepository.savedUsrMoney("SIMIT");
    }

    public Integer graduatedNum(){
        return paymentsRepository.graduatedUsr();
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
