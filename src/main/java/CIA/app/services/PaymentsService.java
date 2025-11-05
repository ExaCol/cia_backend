package CIA.app.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import CIA.app.model.Payments;
import CIA.app.repositories.PaymentsRepository;
import CIA.app.repositories.UsrRepository;

@Service
public class PaymentsService {

    @Autowired
    private PaymentsRepository paymentsRepository;
    @Autowired
    private UsrRepository usrRepository;

    public PaymentsService(PaymentsRepository paymentsRepository, UsrRepository usrRepository) {
        this.paymentsRepository = paymentsRepository;
        this.usrRepository = usrRepository;
    }

    public Payments getSpecificPayments(Integer paymentId) {
        Optional<Payments> p = paymentsRepository.findById(paymentId);
        return p.orElse(null);
    }

    public List<Payments> paymentHistoryByUsr(Integer usrId) {
        return usrRepository.findById(usrId).get().getPayments();
    }

    public Payments deleteEspecificPayments(Payments payment) {
        Payments p = getSpecificPayments(payment.getId());
        if (p != null) {
            paymentsRepository.delete(p);
            return p;
        }

        return null;
    }

    public int getPaymentNumber(LocalDate startDate, LocalDate endDate) {
        return paymentsRepository.findPaymentsNumBetweenDates(startDate, endDate);
    }

    public Double earningsByCat(String type) {
        return paymentsRepository.earningsByCat(type);
    }

    public Double getNetWorth() {
        return paymentsRepository.findTotalPaymentsAmount();
    }

    public Double savedUsrMoney() {
        return paymentsRepository.savedUsrMoney("TICKET");
    }

    public Integer graduatedNum() {
        return paymentsRepository.graduatedUsr();
    }
}
