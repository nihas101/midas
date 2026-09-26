package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import de.nihas101.midas.core.shareholders.service.ShareholdersService;
import de.nihas101.midas.headless.rest.dto.ShareholderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shareholders")
@RequiredArgsConstructor
public class ShareholdersController {

    private final ShareholdersService shareholdersService;

    @GetMapping
    public List<ShareholderDto> getAllShareholders() {
        return shareholdersService.shareholders().toList().stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShareholderDto> getShareholder(@PathVariable final Integer id) {
        final Shareholder sh = shareholdersService.shareholder(id);
        if (sh == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(sh));
    }

    @PostMapping
    public ResponseEntity<ShareholderDto> createShareholder(@RequestBody final ShareholderDto dto) {
        final DefaultShareholder entity = new DefaultShareholder(
                null,
                dto.getDisplayId(),
                dto.getFirstName(),
                dto.getLastName()
        );
        shareholdersService.create(entity);

        // Find created shareholder by matching details to return ID
        final Shareholder created = shareholdersService.shareholders().toList().stream()
                .filter(s -> s.getFirstName().equals(dto.getFirstName()) && s.getLastName().equals(dto.getLastName()))
                .reduce((first, second) -> second)
                .orElse(entity);

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShareholderDto> updateShareholder(
            @PathVariable final Integer id,
            @RequestBody final ShareholderDto dto
    ) {
        final Shareholder existing = shareholdersService.shareholder(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        final DefaultShareholder toUpdate = new DefaultShareholder(
                id,
                dto.getDisplayId() != null ? dto.getDisplayId() : existing.getDisplayId(),
                dto.getFirstName() != null ? dto.getFirstName() : existing.getFirstName(),
                dto.getLastName() != null ? dto.getLastName() : existing.getLastName()
        );
        shareholdersService.update(toUpdate);
        return ResponseEntity.ok(toDto(shareholdersService.shareholder(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShareholder(@PathVariable final Integer id) {
        final Shareholder sh = shareholdersService.shareholder(id);
        if (sh == null) {
            return ResponseEntity.notFound().build();
        }
        shareholdersService.delete(sh);
        return ResponseEntity.noContent().build();
    }

    private ShareholderDto toDto(final Shareholder s) {
        return ShareholderDto.builder()
                .id(s.getId())
                .displayId(s.getDisplayId())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .build();
    }
}
