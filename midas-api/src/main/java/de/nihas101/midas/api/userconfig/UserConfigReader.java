package de.nihas101.midas.api.userconfig;

import java.util.Optional;

public interface UserConfigReader {
    Optional<UserConfig> findByUserIdentifier(final String userIdentifier);
}
