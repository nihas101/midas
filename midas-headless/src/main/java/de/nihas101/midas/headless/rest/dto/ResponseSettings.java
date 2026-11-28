package de.nihas101.midas.headless.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSettings {

    @Schema(description = "User identifier", example = "default-user")
    private String userIdentifier;

    @Schema(description = "UI Theme", example = "dark")
    private String theme;

    @Schema(description = "Locale language tag", example = "en")
    private Locale locale;
}
