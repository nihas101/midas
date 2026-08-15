package de.nihas101.midas.core.userconfig.dto;

import de.nihas101.midas.api.userconfig.UserConfig;
import de.nihas101.midas.core.userconfig.entity.UserConfigEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultUserConfig implements UserConfig {

    private int id;

    private String userIdentifier;

    private String theme;

    private String locale;

    public DefaultUserConfig(final String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public static UserConfig fromEntity(final UserConfigEntity userConfigEntity) {
        return new DefaultUserConfig(
                userConfigEntity.getId(),
                userConfigEntity.getUserIdentifier(),
                userConfigEntity.getTheme(),
                userConfigEntity.getLocale()
        );
    }
}
