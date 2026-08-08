package de.nihas101.midas.core.cleanup;

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

    private final BookingsCleanup bookingsCleanup;

    @Scheduled(fixedDelayString = "#{@midasConfig.cleanup.delayBetweenCleanups}")
    public void cleanUp() {
        bookingsCleanup.cleanUp();
    }
}
