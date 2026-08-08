package de.nihas101.midas.persistance.backup;

import org.springframework.stereotype.Component;

@Component
public interface DatabaseLocationFactory {

    DatabaseLocation create();
}
