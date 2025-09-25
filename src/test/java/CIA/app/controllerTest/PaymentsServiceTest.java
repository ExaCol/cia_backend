package CIA.app.controllerTest;

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
import CIA.app.services.PaymentsService;
import CIA.app.services.UsrService;

@ExtendWith(MockitoExtension.class)
public class PaymentsServiceTest {

    @Mock
    private PaymentsRepository paymentsRepository;

    @Mock
    private UsrService usrService;

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
        p.setreleaseDate(releaseDate);
        p.setState(state);
        p.setAmount(amount);
        return p;
    }

    @Test
    void getPaymentHistoryByUserId_ok() {
        Integer id = 1;
        String email = "ana@exa.co";
        List<Payments> entities = List.of(
            payment(1 ,LocalDate.of(2025,9,10), 150000, "APPROVED"),
            payment(2,LocalDate.of(2025,9,1)         ,80000,  "APPROVED")
        );
        when(usrService.findByEmail(email)).thenReturn(usr(1, email));
        when(paymentsRepository.findAllByUserServicesOrderByReleaseDateDesc(id)).thenReturn(entities);

        List<Payments> out = paymentsService.getPaymentHistoryByUserId(email);

        assertEquals(2, out.size());
        assertEquals(1, out.get(0).getId()); 
        assertEquals("APPROVED", out.get(0).getState());
        verify(paymentsRepository).findAllByUserServicesOrderByReleaseDateDesc(id);
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
        String category = "SIMIT";
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

        when(paymentsRepository.savedUsrMoney("SIMIT")).thenReturn(expectedSavings);

        Double actualSavings = paymentsService.savedUsrMoney();

        assertEquals(expectedSavings, actualSavings);
        verify(paymentsRepository).savedUsrMoney("SIMIT");
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

