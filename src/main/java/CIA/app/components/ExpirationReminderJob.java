package CIA.app.components;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import CIA.app.services.ExpirationReminderService;

@Component
public class ExpirationReminderJob {
    private final ExpirationReminderService reminderService;

    public ExpirationReminderJob(ExpirationReminderService reminderService) {
        this.reminderService = reminderService;
    }

    // Todos los días a las 12:00 m. Zona horaria Bogotá (UTC-5, sin DST)
    //@Scheduled(cron = "0 */1 * * * *", zone = "America/Bogota")
    @Scheduled(cron = "0 0 12 * * *", zone = "America/Bogota")
    public void runDailyReminder() {
        reminderService.sendMonthlyExpiringReminders();
    }
}