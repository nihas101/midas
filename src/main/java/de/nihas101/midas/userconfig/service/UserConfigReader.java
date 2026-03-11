package de.nihas101.midas.userconfig.service;

import de.nihas101.midas.userconfig.entity.UserConfig;

import java.util.Optional;

public interface UserConfigReader {
    Optional<UserConfig> findByUserIdentifier(final String userIdentifier);
}
