package de.nihas101.midas.core.userconfig.service;

import de.nihas101.midas.core.userconfig.entity.UserConfig;

public interface UserConfigWriter {
    void save(final UserConfig userConfig);
}
