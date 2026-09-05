package de.nihas101.midas.core.userconfig.dto;

import de.nihas101.midas.api.userconfig.UserConfig;
import de.nihas101.midas.core.userconfig.entity.UserConfigEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DefaultUserConfigTest {

    @Test
    void fromEntity_mapsAllFieldsCorrectly() {
        final UserConfigEntity entity = UserConfigEntity.builder()
                .id(42)
                .userIdentifier("user123")
                .theme("dark")
                .locale("de")
                .build();

        final UserConfig config = DefaultUserConfig.fromEntity(entity);

        assertEquals(42, config.getId());
        assertEquals("user123", config.getUserIdentifier());
        assertEquals("dark", config.getTheme());
        assertEquals("de", config.getLocale());
    }

    @Test
    void constructorWithUserIdentifier_setsIdentifierAndDefaults() {
        final DefaultUserConfig config = new DefaultUserConfig("bob");

        assertEquals(0, config.getId());
        assertEquals("bob", config.getUserIdentifier());
        assertNull(config.getTheme());
        assertNull(config.getLocale());
    }
}
