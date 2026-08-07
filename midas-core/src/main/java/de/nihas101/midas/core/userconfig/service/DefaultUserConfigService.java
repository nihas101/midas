package de.nihas101.midas.core.userconfig.service;

import de.nihas101.midas.api.userconfig.UserConfig;
import de.nihas101.midas.api.userconfig.UserConfigService;
import de.nihas101.midas.core.userconfig.dto.DefaultUserConfig;
import de.nihas101.midas.core.userconfig.entity.UserConfigEntity;
import de.nihas101.midas.core.userconfig.repository.UserConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultUserConfigService implements UserConfigService { // TODO: Test

    private final UserConfigRepository userConfigRepository;

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
