package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.userconfig.UserConfig;
import de.nihas101.midas.api.userconfig.UserConfigFactory;
import de.nihas101.midas.api.userconfig.UserConfigService;
import de.nihas101.midas.core.userconfig.dto.DefaultUserConfig;
import de.nihas101.midas.headless.config.MidasConfig;
import de.nihas101.midas.headless.rest.dto.RequestSettings;
import de.nihas101.midas.headless.rest.dto.ResponseSettings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
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
@Tag(name = "Settings", description = "Controller for settings CRUD")
public class SettingsController {

    private final UserConfigService userConfigService;
    private final UserConfigFactory userConfigFactory;
    private final MidasConfig config;

    @Operation(
            summary = "Get settings for the default user",
            description = "Returns the settings for the default user."
    )
    @GetMapping
    public ResponseEntity<ResponseSettings> getSettings() {
        final UserConfig userConfig = userConfigService.findByUserIdentifier(UserConfigService.DEFAULT_USER)
                .orElseGet(() -> userConfigFactory.create(UserConfigService.DEFAULT_USER));

        final String theme = userConfig.getTheme() != null
                ? userConfig.getTheme()
                : config.getTheme().getDefaultTheme();
        final String localeStr = userConfig.getLocale() != null && !userConfig.getLocale().isEmpty()
                ? userConfig.getLocale()
                : config.getI18n().getDefaultLocale();

        final ResponseSettings dto = ResponseSettings.builder()
                .id(userConfig.getId())
                .theme(theme)
                .locale(Locale.of(localeStr))
                .build();
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Update settings for the default user",
            description = "Updates the settings for the default user."
    )
    @PutMapping
    public ResponseEntity<ResponseSettings> updateSettings(
            @NotNull
            @RequestBody final RequestSettings dto
    ) {
        final UserConfig existing = userConfigService.findByUserIdentifier(UserConfigService.DEFAULT_USER)
                .orElseGet(() -> userConfigFactory.create(UserConfigService.DEFAULT_USER));
        final UserConfig updated = new DefaultUserConfig(
                existing.getId(),
                UserConfigService.DEFAULT_USER,
                dto.getTheme() != null ? dto.getTheme() : existing.getTheme(),
                dto.getLocale() != null ? dto.getLocale().toString() : existing.getLocale()
        );
        userConfigService.save(updated);

        final ResponseSettings response = ResponseSettings.builder()
                .id(updated.getId())
                .theme(updated.getTheme())
                .locale(updated.getLocale() != null ? Locale.of(updated.getLocale()) : null)
                .build();

        return ResponseEntity.ok(response);
    }
}
