package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.shareholder.ShareholderFactory;
import de.nihas101.midas.api.shareholder.Shareholders;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import de.nihas101.midas.core.shareholders.service.ShareholdersService;
import de.nihas101.midas.headless.config.MidasConfig;
import de.nihas101.midas.headless.rest.dto.RequestShareholder;
import de.nihas101.midas.headless.rest.dto.ResponseShareholder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@Tag(name = "Shareholders", description = "Controller for shareholders CRUD")
public class ShareholdersController {

    private final ShareholdersService shareholdersService;
    private final ShareholderFactory shareholderFactory;
    private final MidasConfig config;

    @Operation(
            summary = "Get all shareholders",
            description = "Returns a list of all shareholders."
    )
    @GetMapping
    public ResponseEntity<List<ResponseShareholder>> getAllShareholders() {
        final Shareholders shareholders = shareholdersService.shareholders();
        final List<ResponseShareholder> dtos = shareholders.toList()
                .stream()
                .map(ResponseShareholder::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(
            summary = "Get a shareholder by ID",
            description = "Returns a shareholder by the specified ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ResponseShareholder> getShareholder(
            @NotNull
            @Positive
            @PathVariable final Integer id
    ) {
        final Shareholder sh = shareholdersService.shareholder(id);
        if (sh == null) {
            return ResponseEntity.notFound()
                    .build();
        }
        return ResponseEntity.ok(ResponseShareholder.toDto(sh));
    }

    @Operation(
            summary = "Create a new shareholder",
            description = "Creates a new shareholder."
    )
    @PostMapping
    public ResponseEntity<ResponseShareholder> createShareholder(
            @NotNull
            @RequestBody final RequestShareholder dto
    ) {
        final DefaultShareholder toCreate = new DefaultShareholder(
                null,
                dto.getDisplayId(),
                dto.getFirstName(),
                dto.getLastName()
        );
        shareholdersService.create(toCreate);

        final Shareholder saved = shareholdersService.shareholders().toList().stream()
                .filter(s -> dto.getFirstName().equals(s.getFirstName()) && dto.getLastName().equals(s.getLastName()))
                .reduce((first, second) -> second)
                .orElse(toCreate);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseShareholder.toDto(saved));
    }

    @Operation(
            summary = "Update a shareholder",
            description = "Updates the info of the shareholder with the specified ID."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ResponseShareholder> updateShareholder(
            @NotNull
            @Positive
            @PathVariable final Integer id,

            @NotNull
            @RequestBody final RequestShareholder requestShareholder
    ) {
        final Shareholder shareholder = shareholdersService.shareholder(id);
        if (shareholder == null) {
            return ResponseEntity.notFound()
                    .build();
        }

        final Shareholder toUpdate = updateShareholder(shareholder, requestShareholder);
        shareholdersService.update(toUpdate);
        return ResponseEntity.ok(ResponseShareholder.toDto(shareholdersService.shareholder(id)));
    }

    private Shareholder updateShareholder(
            final Shareholder shareholder,
            final RequestShareholder requestShareholder
    ) {
        final Shareholder sh = shareholderFactory.create();
        sh.setId(shareholder.getId());
        sh.setDisplayId(
                requestShareholder.getDisplayId() != null
                        ? requestShareholder.getDisplayId()
                        : shareholder.getDisplayId()
        );
        sh.setFirstName(
                requestShareholder.getFirstName() != null
                        ? requestShareholder.getFirstName()
                        : shareholder.getFirstName());
        sh.setLastName(
                requestShareholder.getLastName() != null
                        ? requestShareholder.getLastName()
                        : shareholder.getLastName()
        );
        return sh;
    }

    @Operation(
            summary = "Delete a shareholder",
            description = "Deletes the shareholder with the specified ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShareholder(
            @NotNull
            @Positive
            @PathVariable final Integer id
    ) {
        final Shareholder sh = shareholdersService.shareholder(id);
        if (sh == null) {
            return ResponseEntity.notFound()
                    .build();
        }
        shareholdersService.delete(
                sh,
                config.getShareholder().getDeleteMode(),
                config.bookingActivePeriodStart(),
                config.bookingActivePeriodEnd()
        );
        return ResponseEntity.noContent()
                .build();
    }
}
