package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.userconfig.UserConfig;
import de.nihas101.midas.api.userconfig.UserConfigFactory;
import de.nihas101.midas.api.userconfig.UserConfigService;
import de.nihas101.midas.core.config.I18nConfig;
import de.nihas101.midas.core.config.ThemeConfig;
import de.nihas101.midas.core.userconfig.dto.DefaultUserConfig;
import de.nihas101.midas.headless.rest.dto.SettingsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final UserConfigService userConfigService;
    private final UserConfigFactory userConfigFactory;
    private final ThemeConfig themeConfig;
    private final I18nConfig i18nConfig;

    @GetMapping
    public ResponseEntity<SettingsDto> getSettings() {
        final UserConfig userConfig = userConfigFactory.create(UserConfigService.DEFAULT_USER);
        final String theme = resolveTheme(userConfig);
        final Locale locale = resolveLocale(userConfig);

        return ResponseEntity.ok(SettingsDto.builder()
                .theme(theme)
                .locale(locale)
                .build());
    }

    private String resolveTheme(final UserConfig userConfig) {
        final String theme = userConfig.getTheme();
        return theme != null ? theme : themeConfig.getDefaultTheme();
    }

    private Locale resolveLocale(final UserConfig userConfig) {
        final String locale = userConfig.getLocale();
        return locale != null ? Locale.of(locale) : Locale.of(i18nConfig.getDefaultLocale());
    }

    @PutMapping
    public ResponseEntity<SettingsDto> updateSettings(@RequestBody final SettingsDto dto) {
        final UserConfig existing = userConfigFactory.create(UserConfigService.DEFAULT_USER);
        final UserConfig updated = new DefaultUserConfig(
                existing.getId(),
                UserConfigService.DEFAULT_USER,
                dto.getTheme() != null ? dto.getTheme() : existing.getTheme(),
                dto.getLocale() != null ? dto.getLocale().toString() : existing.getLocale()
        );
        userConfigService.save(updated);

        return getSettings();
    }
}
