package de.nihas101.midas.userconfig.service;

import de.nihas101.midas.userconfig.entity.UserConfig;
import de.nihas101.midas.userconfig.repository.UserConfigRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserConfigService implements UserConfigReader, UserConfigWriter {

    public static final String DEFAULT_USER = "default-user";

    private final UserConfigRepository userConfigRepository;

    public UserConfigService(UserConfigRepository userConfigRepository) {
        this.userConfigRepository = userConfigRepository;
    }

    @Override
    public Optional<UserConfig> findByUserIdentifier(String userIdentifier) {
        return userConfigRepository.findByUserIdentifier(userIdentifier);
    }

    @Override
    public void save(UserConfig userConfig) {
        userConfigRepository.save(userConfig);
    }
}
