package de.nihas101.midas.shareholders.dto;

import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShareholderTest {

    @Test
    void fromEntity_withDisplayId() {
        ShareholderEntity entity = new ShareholderEntity();
        entity.setId(1);
        entity.setDisplayId(100);
        entity.setFirstName("Max");
        entity.setLastName("Mustermann");
        entity.setExternalId(500);

        Shareholder dto = Shareholder.fromEntity(entity);

        assertEquals(1, dto.getId());
        assertEquals(100, dto.getDisplayId());
        assertEquals("Max", dto.getFirstName());
        assertEquals("Mustermann", dto.getLastName());
        assertEquals(500, dto.getExternalId());
    }

    @Test
    void fromEntity_withoutDisplayId_fallsBackToId() {
        ShareholderEntity entity = new ShareholderEntity();
        entity.setId(42);
        entity.setDisplayId(null);
        entity.setFirstName("Erika");
        entity.setLastName("Musterfrau");

        Shareholder dto = Shareholder.fromEntity(entity);

        assertEquals(42, dto.getId());
        assertEquals(42, dto.getDisplayId());
    }
}
