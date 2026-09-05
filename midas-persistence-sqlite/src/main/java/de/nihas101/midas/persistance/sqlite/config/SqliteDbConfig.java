package de.nihas101.midas.persistance.sqlite.config;

import de.nihas101.midas.persistance.DbConfig;
import lombok.Data;

@Data
public class SqliteDbConfig implements DbConfig {
    private SqliteConfig sqlite;
}
