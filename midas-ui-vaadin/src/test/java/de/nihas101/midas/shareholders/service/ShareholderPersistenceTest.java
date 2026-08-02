package de.nihas101.midas.shareholders.service;

import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import de.nihas101.midas.core.shareholders.repository.ShareholdersRepository;
import de.nihas101.midas.core.shareholders.service.ShareholdersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@ActiveProfiles("test")
class ShareholderPersistenceTest { // TODO: Figure out a way to move this into the core

    @Autowired
    private ShareholdersService service;

    @Autowired
    private ShareholdersRepository repository;

    @Test
    void create_withDisplayId_shouldPersistDisplayId() {
        // Given
        int customDisplayId = 999;
        Shareholder dto = new DefaultShareholder(null, customDisplayId, "John", "Doe");

        // When
        service.create(dto);

        // Then
        Shareholder saved = service.shareholders().toList().stream()
                .filter(s -> s.getFirstName().equals("John") && s.getLastName().equals("Doe"))
                .findFirst()
                .orElseThrow();

        // This is expected to FAIL currently because of insertable = false
        assertEquals(customDisplayId, saved.getDisplayId(), "The custom display ID should have been persisted");
    }

    @Test
    void update_withDisplayId_shouldPersistDisplayId() {
        // Given
        Shareholder dto = new DefaultShareholder(null, null, "Jane", "Doe");
        service.create(dto);

        Shareholder saved = service.shareholders().toList().stream()
                .filter(s -> s.getFirstName().equals("Jane") && s.getLastName().equals("Doe"))
                .findFirst()
                .orElseThrow();

        assertEquals(saved.getId(), saved.getDisplayId(), "If no display ID is provided, it should default to the database ID");

        int originalDisplayId = saved.getDisplayId();
        int newDisplayId = 888;
        saved.setDisplayId(newDisplayId);

        // When
        service.update(saved);

        // Then
        Shareholder updated = service.shareholder(saved.getId());
        assertEquals(newDisplayId, updated.getDisplayId(), "The new display ID should have been persisted during update");
        assertNotEquals(originalDisplayId, updated.getDisplayId());
    }
}
