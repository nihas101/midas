package de.nihas101.midas.core.sqlite;

import lombok.Data;

@Data
public class SqliteConfig {
    private Optimize optimize = new Optimize();

    private static class Optimize {
        boolean enabled = true;
    }
}
