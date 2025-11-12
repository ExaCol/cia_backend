package CIA.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import CIA.app.model.Payments;
import CIA.app.model.Usr;
import CIA.app.repositories.PaymentsRepository;
import CIA.app.repositories.UsrRepository;
import CIA.app.services.PaymentsService;
import CIA.app.services.UsrService;

@ExtendWith(MockitoExtension.class)
public class PaymentsServiceTest {

    @Mock
    private PaymentsRepository paymentsRepository;

    @Mock
    private UsrService usrService;

    @Mock
    private UsrRepository usrRepository;

    @InjectMocks
    private PaymentsService paymentsService;


    private static Usr usr(int id, String email) {
        Usr u = new Usr();
        u.setId(id);
        u.setEmail(email);
        return u;
    }

    private Payments payment(Integer id,LocalDate releaseDate, int amount, String state) {
        Payments p = new Payments();
        p.setId(id);
        p.setReleaseDate(releaseDate);
        p.setState(state);
        p.setAmount(amount);
        return p;
    }

    @Test
    void getSpecificPayments_ok() {
        Integer paymentId = 1;
        Payments expectedPayment = payment(paymentId, LocalDate.of(2023, 5, 1), 100000, "COMPLETED");

        when(paymentsRepository.findById(paymentId)).thenReturn(java.util.Optional.of(expectedPayment));

        Payments actualPayment = paymentsService.getSpecificPayments(paymentId);

        assertEquals(expectedPayment, actualPayment);
        verify(paymentsRepository).findById(paymentId);
    }

    @Test
    void paymentHistoryByUsr_ok() {
        Integer userId = 1;
        String email = "nico@exa.co";
        Usr user = usr(userId, email);
        
        List<Payments> expectedPayments = List.of(
                payment(1, LocalDate.of(2023, 5, 1), 100000, "COMPLETED"),
                payment(2, LocalDate.of(2023, 6, 1), 150000, "PENDING")
        );
        user.setPayments(expectedPayments);

        when(usrRepository.findById(userId)).thenReturn(java.util.Optional.of(user));


        List<Payments> actualPayments = paymentsService.paymentHistoryByUsr(userId);

        assertEquals(expectedPayments.size(), actualPayments.size());
        verify(usrRepository).findById(userId);
    }


    @Test
    void deleteEspecificPayments_ok() {
        Integer paymentId = 1;
        Payments paymentToDelete = payment(paymentId, LocalDate.of(2023, 5, 1), 100000, "COMPLETED");

        when(paymentsRepository.findById(paymentId)).thenReturn(java.util.Optional.of(paymentToDelete));

        Payments deletedPayment = paymentsService.deleteEspecificPayments(paymentToDelete);

        assertEquals(paymentToDelete, deletedPayment);
        verify(paymentsRepository).delete(paymentToDelete);
    }

    @Test
    void getPaymentNumber_ok() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        int expectedNumber = 5;

        when(paymentsRepository.findPaymentsNumBetweenDates(startDate, endDate)).thenReturn(expectedNumber);

        int actualNumber = paymentsService.getPaymentNumber(startDate, endDate);

        assertEquals(expectedNumber, actualNumber);
        verify(paymentsRepository).findPaymentsNumBetweenDates(startDate, endDate);
    }

    @Test
    void earningsByCat_ok() {
        String category = "TICKET";
        Double expectedEarnings = 500000.0;

        when(paymentsRepository.earningsByCat(category)).thenReturn(expectedEarnings);

        Double actualEarnings = paymentsService.earningsByCat(category);

        assertEquals(expectedEarnings, actualEarnings);
        verify(paymentsRepository).earningsByCat(category);
    }

    @Test
    void getNetWorth_ok() {
        Double expectedNetWorth = 2000000.0;

        when(paymentsRepository.findTotalPaymentsAmount()).thenReturn(expectedNetWorth);

        Double actualNetWorth = paymentsService.getNetWorth();

        assertEquals(expectedNetWorth, actualNetWorth);
        verify(paymentsRepository).findTotalPaymentsAmount();
    }

    @Test
    void savedUsrMoney_ok() {
        Double expectedSavings = 300000.0;

        when(paymentsRepository.savedUsrMoney("TICKET")).thenReturn(expectedSavings);

        Double actualSavings = paymentsService.savedUsrMoney();

        assertEquals(expectedSavings, actualSavings);
        verify(paymentsRepository).savedUsrMoney("TICKET");
    }

    @Test
    void graduatedNum_ok() {
        Integer expectedGraduates = 10;

        when(paymentsRepository.graduatedUsr()).thenReturn(expectedGraduates);

        Integer actualGraduates = paymentsService.graduatedNum();

        assertEquals(expectedGraduates, actualGraduates);
        verify(paymentsRepository).graduatedUsr();
    }
        
}

