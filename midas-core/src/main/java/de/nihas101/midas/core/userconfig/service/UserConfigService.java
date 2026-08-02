package de.nihas101.midas.core.userconfig.service;

import de.nihas101.midas.api.userconfig.UserConfig;
import de.nihas101.midas.api.userconfig.UserConfigWriter;
import de.nihas101.midas.core.userconfig.dto.DefaultUserConfig;
import de.nihas101.midas.core.userconfig.entity.UserConfigEntity;
import de.nihas101.midas.core.userconfig.repository.UserConfigRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserConfigService implements de.nihas101.midas.api.userconfig.UserConfigReader, UserConfigWriter { // TODO: Test

    public static final String DEFAULT_USER = "default-user";

    private final UserConfigRepository userConfigRepository;

    public UserConfigService(final UserConfigRepository userConfigRepository) {
        this.userConfigRepository = userConfigRepository;
    }

    @Override
    public Optional<UserConfig> findByUserIdentifier(final String userIdentifier) {
        return userConfigRepository.findByUserIdentifier(userIdentifier)
                .map(DefaultUserConfig::fromEntity);
    }

    @Override
    public void save(final UserConfig userConfig) {
        userConfigRepository.save(UserConfigEntity.fromDto(userConfig));
    }
}
