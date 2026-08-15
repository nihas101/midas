package de.nihas101.midas.core.userconfig.dto;

import de.nihas101.midas.api.userconfig.UserConfig;
import de.nihas101.midas.core.userconfig.entity.UserConfigEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultUserConfigTest {

    @Test
    void mapsFromEntityCorrectly() {
        final UserConfigEntity entity = new UserConfigEntity(1, "user123", "dark", "de");

        final UserConfig userConfig = DefaultUserConfig.fromEntity(entity);

        assertEquals(1, userConfig.getId());
        assertEquals("user123", userConfig.getUserIdentifier());
        assertEquals("dark", userConfig.getTheme());
        assertEquals("de", userConfig.getLocale());
    }
}
