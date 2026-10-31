package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.lock.LockReader;
import de.nihas101.midas.api.lock.LockWriter;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.shareholder.ShareholdersReader;
import de.nihas101.midas.headless.rest.dto.RequestLock;
import de.nihas101.midas.headless.rest.dto.ResponseLock;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;

@RestController
@RequestMapping("/api/v1/locks")
@RequiredArgsConstructor
@Tag(name = "Locks", description = "Controller for locking/unlocking years for shareholders.")
public class LocksController {

    private final LockReader lockReader;
    private final LockWriter lockWriter;
    private final ShareholdersReader shareholdersReader;

    @Operation(
            summary = "Get the lock status of a shareholder and year",
            description = "Returns the lock status for the specified shareholder and year."
    )
    @GetMapping
    public ResponseEntity<ResponseLock> getLockStatus(
            @NotNull
            @Positive
            @RequestParam final Integer shareholderId,

            @NotNull
            @Positive
            @RequestParam final Integer year
    ) {
        final Shareholder shareholder = shareholdersReader.shareholder(shareholderId);
        if (shareholder == null) {
            return ResponseEntity.notFound()
                    .build();
        }

        final boolean locked = lockReader.isLocked(shareholder, Year.of(year));
        return ResponseEntity.ok(
                ResponseLock.builder()
                        .shareholderId(shareholderId)
                        .year(Year.of(year))
                        .locked(locked)
                        .build()
        );
    }

    @Operation(
            summary = "Lock/Unlock a year for a shareholder",
            description = "Locks/Unlocks the lock for the specified shareholder and year."
    )
    @PostMapping
    public ResponseEntity<Void> lockUnlock(
            @NotNull
            @RequestBody final RequestLock lock
    ) {
        if (lock.getShareholderId() == null || lock.getYear() == null) {
            return ResponseEntity.badRequest()
                    .build();
        }
        final Shareholder shareholder = shareholdersReader.shareholder(lock.getShareholderId());
        if (shareholder == null) {
            return ResponseEntity.notFound()
                    .build();
        }

        if (lock.isLocked()) {
            lockWriter.lock(shareholder, lock.getYear());
        } else {
            lockWriter.unlock(shareholder, lock.getYear());
        }
        return ResponseEntity.ok()
                .build();
    }
}
