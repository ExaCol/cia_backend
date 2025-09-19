package CIA.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import CIA.app.model.Usr;
import CIA.app.model.Vehicle;
import CIA.app.repositories.VehicleRepository;

@ExtendWith(MockitoExtension.class)
public class ExpirationReminderServiceTest {
    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ExpirationReminderService service;

    private Usr user(String name, String email) {
        Usr u = new Usr();
        u.setName(name);
        u.setEmail(email);
        return u;
    }

    private Vehicle vehicle(String plate, String type, LocalDate soat, LocalDate tecno, Usr owner, String rateType) {
        Vehicle v = new Vehicle();
        v.setPlate(plate);
        v.setType(type);
        v.setSoatExpiration(soat != null ? Date.valueOf(soat) : null);
        v.setTechnoExpiration(tecno != null ? Date.valueOf(tecno) : null);
        v.setUsr(owner);
        v.setSoatRateType(rateType);
        return v;
    }

    @Test
    void sendMonthlyExpiringReminders_sendsOneEmail_perUser_andFormatsBody() {
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusMonths(1);
        Date expectedParam = Date.valueOf(limit);

        Usr u1 = user("Ana", "ana@exa.co");
        Vehicle v1 = vehicle("ABC123", "car",  today.plusDays(10), today.plusDays(20), u1, "A");
        Vehicle v2 = vehicle("XYZ987", "moto", today.plusDays(5),  null,               u1, "B");

        when(vehicleRepository.findVehiclesExpiringBy(any(Date.class)))
                .thenReturn(List.of(v1, v2));

        ArgumentCaptor<String> toCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCap = ArgumentCaptor.forClass(String.class);

        service.sendMonthlyExpiringReminders();

        ArgumentCaptor<Date> dateCap = ArgumentCaptor.forClass(Date.class);
        verify(vehicleRepository).findVehiclesExpiringBy(dateCap.capture());
        assertEquals(expectedParam, dateCap.getValue(), "Debe consultar con hoy+1 mes");

        verify(emailService, times(1)).enviarCorreo(toCap.capture(), subjectCap.capture(), bodyCap.capture());

        assertEquals("ana@exa.co", toCap.getValue());
        assertEquals("Recordatorio: vencimientos próximos de SOAT/Tecnomecánica", subjectCap.getValue());

        String body = bodyCap.getValue();
        assertNotNull(body);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        assertTrue(body.contains("Hola Ana"), "Debe saludar por nombre");
        assertTrue(body.contains("ABC123"), "Debe listar placa ABC123");
        assertTrue(body.contains("XYZ987"), "Debe listar placa XYZ987");
        assertTrue(body.contains(fmt.format(today.plusDays(10))), "Debe incluir fecha SOAT de ABC123");
        assertTrue(body.contains("día(s)"), "Debe indicar días restantes");
        assertTrue(body.contains("Tarifa SOAT"), "Debe incluir encabezado de tarifa");
        assertTrue(body.contains("Equipo SmartTraffic"), "Firma esperada");
    }

    @Test
    void sendMonthlyExpiringReminders_groupsByUser_sendsTwoEmails() {
        LocalDate today = LocalDate.now();
        Usr u1 = user("Ana", "ana@exa.co");
        Usr u2 = user("Luis", "luis@exa.co");

        Vehicle v1 = vehicle("AAA111", "car",  today.plusDays(7),  today.plusDays(9),  u1, "A");
        Vehicle v2 = vehicle("BBB222", "moto", today.plusDays(12), null,               u2, "B");

        when(vehicleRepository.findVehiclesExpiringBy(any(Date.class)))
                .thenReturn(List.of(v1, v2));

        service.sendMonthlyExpiringReminders();

        verify(emailService, times(1)).enviarCorreo(eq("ana@exa.co"), anyString(), contains("AAA111"));
        verify(emailService, times(1)).enviarCorreo(eq("luis@exa.co"), anyString(), contains("BBB222"));
        verify(emailService, times(2)).enviarCorreo(anyString(), anyString(), anyString());
    }
}
