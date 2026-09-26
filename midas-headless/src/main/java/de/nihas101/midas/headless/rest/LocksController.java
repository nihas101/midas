package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.lock.LockReader;
import de.nihas101.midas.api.lock.LockWriter;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.shareholders.service.ShareholdersService;
import de.nihas101.midas.headless.rest.dto.LockDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class LocksController {

    private final LockReader lockReader;
    private final LockWriter lockWriter;
    private final ShareholdersService shareholdersService;

    @GetMapping
    public ResponseEntity<LockDto> getLockStatus(
            @RequestParam final Integer shareholderId,
            @RequestParam final Integer year
    ) {
        final Shareholder shareholder = shareholdersService.shareholder(shareholderId);
        if (shareholder == null) {
            return ResponseEntity.notFound().build();
        }

        final boolean locked = lockReader.isLocked(shareholder, Year.of(year));
        return ResponseEntity.ok(LockDto.builder()
                .shareholderId(shareholderId)
                .year(Year.of(year))
                .locked(locked)
                .build());
    }

    @PostMapping
    public ResponseEntity<Void> lock(@RequestBody final LockDto lockDto) {
        final Shareholder shareholder = shareholdersService.shareholder(lockDto.getShareholderId());
        if (shareholder == null) {
            return ResponseEntity.notFound().build();
        }

        lockWriter.lock(shareholder, lockDto.getYear());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> unlock(
            @RequestParam final Integer shareholderId,
            @RequestParam final Integer year
    ) {
        final Shareholder shareholder = shareholdersService.shareholder(shareholderId);
        if (shareholder == null) {
            return ResponseEntity.notFound().build();
        }

        lockWriter.unlock(shareholder, Year.of(year));
        return ResponseEntity.noContent().build();
    }
}
