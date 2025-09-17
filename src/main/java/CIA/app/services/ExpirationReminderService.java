package CIA.app.services;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import CIA.app.model.Usr;
import CIA.app.model.Vehicle;
import CIA.app.repositories.VehicleRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ExpirationReminderService {
    private final VehicleRepository vehicleRepository;
    private final EmailService emailService;

    public ExpirationReminderService(VehicleRepository vehicleRepository, EmailService emailService) {
        this.vehicleRepository = vehicleRepository;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public void sendMonthlyExpiringReminders() {
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusMonths(1);

        List<Vehicle> expiring = vehicleRepository.findVehiclesExpiringBy(Date.valueOf(limit));
        if (expiring.isEmpty()) return;

        Map<Usr, List<Vehicle>> byUser = expiring.stream()
                .collect(Collectors.groupingBy(Vehicle::getUsr));
        byUser.forEach((usr, vehicles) -> {
            try {
                sendEmail(usr, vehicles, today);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void sendEmail(Usr usr, List<Vehicle> vehicles, LocalDate today) {
    final String destinatario = usr.getEmail();
    if (!StringUtils.hasText(destinatario)) return;

    final String asunto = "Recordatorio: vencimientos próximos de SOAT/Tecnomecánica";
    final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    StringBuilder sb = new StringBuilder();
    sb.append("Hola ").append(safe(usr.getName() != null ? usr.getName() : "Usuario")).append(",\n\n");
    sb.append("Estos vehículos tienen vencimientos próximos (≤ 1 mes):\n\n");
    sb.append(String.format("%-10s %-10s %-30s %-30s %-15s%n",
            " Placa", "     Tipo   ", "    SOAT (fecha y días)  ", "   Tecno (fecha y días)", "Tarifa SOAT"));
    sb.append(String.format("%-10s %-10s %-30s %-30s %-15s%n",
            " --------", "      -------- ", "    ------------------------------", "       ------------------------------", "      -----------"));

    for (Vehicle v : vehicles) {
        String soatFecha = "-";
        String soatDias  = "-";
        if (v.getSoatExpiration() != null) {
            var f = v.getSoatExpiration().toLocalDate();
            soatFecha = fmt.format(f);
            long d = java.time.temporal.ChronoUnit.DAYS.between(today, f);
            if (d >= 0) soatDias = d + " día(s)";
        }

        String tecnoFecha = "-";
        String tecnoDias  = "-";
        if (v.getTechnoExpiration() != null) {
            var f = v.getTechnoExpiration().toLocalDate();
            tecnoFecha = fmt.format(f);
            long d = java.time.temporal.ChronoUnit.DAYS.between(today, f);
            if (d >= 0) tecnoDias = d + " día(s)";
        }

        sb.append(String.format("%-10s %-10s %-30s %-30s %-15s%n",
                safe(v.getPlate()),
                safe(v.getType()),
                soatFecha + " (" + soatDias + ")",
                tecnoFecha + " (" + tecnoDias + ")",
                "  " + safe(v.getSoatRateType())));
    }

    sb.append("\nPor favor programa tu renovación con antelación.\n");
    sb.append("— Equipo SmartTraffic\n");

    emailService.enviarCorreo(destinatario, asunto, sb.toString());
}

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
