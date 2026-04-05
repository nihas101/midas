package de.nihas101.midas.backup.service.snapshot;

import java.io.IOException;
import java.util.Properties;

public interface PropertiesLoader {

    Properties load() throws IOException;
}
