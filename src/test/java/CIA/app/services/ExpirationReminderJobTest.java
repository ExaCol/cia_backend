package CIA.app.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import CIA.app.components.ExpirationReminderJob;

public class ExpirationReminderJobTest {
    @Test
    void runDailyReminder_callsService() {
        ExpirationReminderService svc = mock(ExpirationReminderService.class);
        ExpirationReminderJob job = new ExpirationReminderJob(svc);

        job.runDailyReminder();

        verify(svc, times(1)).sendMonthlyExpiringReminders();
    }
}
