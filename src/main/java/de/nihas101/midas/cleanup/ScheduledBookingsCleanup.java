package de.nihas101.midas.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
        name = "midas.cleanup.enabled",
        havingValue = "true",
        matchIfMissing = true
)
@RequiredArgsConstructor
public class ScheduledBookingsCleanup {

    public static final int ONE_HOUR = 3_600_000;
    private final BookingsCleanup bookingsCleanup;

    @Scheduled(fixedDelay = ONE_HOUR)
    public void cleanUp() {
        bookingsCleanup.cleanUp();
    }
}
