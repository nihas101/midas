package de.nihas101.midas.api.backup;

import java.io.IOException;
import java.util.Properties;

public interface PropertiesLoader {

    Properties load() throws IOException;
}
