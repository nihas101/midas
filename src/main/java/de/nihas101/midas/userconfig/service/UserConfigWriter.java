package de.nihas101.midas.userconfig.service;

import de.nihas101.midas.userconfig.entity.UserConfig;

public interface UserConfigWriter {
    void save(UserConfig userConfig);
}
