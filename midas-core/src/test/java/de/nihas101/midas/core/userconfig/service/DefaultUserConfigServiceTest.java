package de.nihas101.midas.core.userconfig.service;

import de.nihas101.midas.api.userconfig.UserConfig;
import de.nihas101.midas.core.userconfig.dto.DefaultUserConfig;
import de.nihas101.midas.core.userconfig.entity.UserConfigEntity;
import de.nihas101.midas.core.userconfig.repository.UserConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultUserConfigServiceTest {

    @Mock
    private UserConfigRepository userConfigRepository;

    @InjectMocks
    private DefaultUserConfigService userConfigService;

    @Test
    void findByUserIdentifier_returnsMappedConfig() {
        final UserConfigEntity entity = UserConfigEntity.builder()
                .id(1)
                .userIdentifier("user-123")
                .theme("dark")
                .locale("de")
                .build();
        when(userConfigRepository.findByUserIdentifier("user-123")).thenReturn(Optional.of(entity));

        final Optional<UserConfig> result = userConfigService.findByUserIdentifier("user-123");

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("user-123", result.get().getUserIdentifier());
        assertEquals("dark", result.get().getTheme());
        assertEquals("de", result.get().getLocale());
    }

    @Test
    void findByUserIdentifier_whenNotFound_returnsEmpty() {
        when(userConfigRepository.findByUserIdentifier("unknown")).thenReturn(Optional.empty());

        final Optional<UserConfig> result = userConfigService.findByUserIdentifier("unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void save_persistsConvertedEntity() {
        final UserConfig config = DefaultUserConfig.builder()
                .id(2)
                .userIdentifier("user-456")
                .theme("light")
                .locale("en")
                .build();

        userConfigService.save(config);

        final ArgumentCaptor<UserConfigEntity> captor = ArgumentCaptor.forClass(UserConfigEntity.class);
        verify(userConfigRepository).save(captor.capture());
        final UserConfigEntity saved = captor.getValue();
        assertEquals(2, saved.getId());
        assertEquals("user-456", saved.getUserIdentifier());
        assertEquals("light", saved.getTheme());
        assertEquals("en", saved.getLocale());
    }
}
