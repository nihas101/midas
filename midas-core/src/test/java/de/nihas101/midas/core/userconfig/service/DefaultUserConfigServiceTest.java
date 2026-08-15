package de.nihas101.midas.core.userconfig.service;

import de.nihas101.midas.api.userconfig.UserConfig;
import de.nihas101.midas.core.userconfig.dto.DefaultUserConfig;
import de.nihas101.midas.core.userconfig.entity.UserConfigEntity;
import de.nihas101.midas.core.userconfig.repository.UserConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultUserConfigServiceTest {

    @Mock
    private UserConfigRepository userConfigRepository;

    @InjectMocks
    private DefaultUserConfigService userConfigService;

    @Test
    void findByUserIdentifierReturnsUserConfigWhenFound() {
        final UserConfigEntity entity = new UserConfigEntity(1, "user123", "dark", "de");
        when(userConfigRepository.findByUserIdentifier("user123")).thenReturn(Optional.of(entity));

        final Optional<UserConfig> result = userConfigService.findByUserIdentifier("user123");

        assertTrue(result.isPresent());
        assertEquals("user123", result.get().getUserIdentifier());
    }

    @Test
    void saveSavesEntityCorrectly() {
        final UserConfig userConfig = new DefaultUserConfig(1, "user123", "light", "en");

        userConfigService.save(userConfig);

        verify(userConfigRepository).save(any(UserConfigEntity.class));
    }
}
