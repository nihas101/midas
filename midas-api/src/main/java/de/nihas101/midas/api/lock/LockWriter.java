package de.nihas101.midas.api.lock;

import de.nihas101.midas.api.shareholder.Shareholder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

public interface LockWriter {

    @Transactional
    void lock(Shareholder shareholder, Year year);

    @Transactional
    void unlock(Shareholder shareholder, Year year);
}
