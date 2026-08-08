package de.nihas101.midas.core.shareholders.dto;

import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShareholderTest {

    @Test
    void fromEntity_withDisplayId() {
        final ShareholderEntity entity = new ShareholderEntity();
        entity.setId(1);
        entity.setDisplayId(100);
        entity.setFirstName("Max");
        entity.setLastName("Mustermann");

        final Shareholder dto = DefaultShareholder.fromEntity(entity);

        assertEquals(1, dto.getId());
        assertEquals(100, dto.getDisplayId());
        assertEquals("Max", dto.getFirstName());
        assertEquals("Mustermann", dto.getLastName());
    }

    @Test
    void fromEntity_withoutDisplayId_fallsBackToId() {
        final ShareholderEntity entity = new ShareholderEntity();
        entity.setId(42);
        entity.setDisplayId(null);
        entity.setFirstName("Erika");
        entity.setLastName("Musterfrau");

        final Shareholder dto = DefaultShareholder.fromEntity(entity);

        assertEquals(42, dto.getId());
        assertEquals(42, dto.getDisplayId());
    }
}
