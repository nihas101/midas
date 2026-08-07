package de.nihas101.midas.core.userconfig.dto;

import de.nihas101.midas.api.userconfig.UserConfig;
import de.nihas101.midas.api.userconfig.UserConfigFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultUserConfigFactory implements UserConfigFactory {

    @Override
    public UserConfig create(final String userIdentifier) {
        return new DefaultUserConfig(userIdentifier);
    }
}
