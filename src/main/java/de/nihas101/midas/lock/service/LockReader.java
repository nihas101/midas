package de.nihas101.midas.lock.service;

import de.nihas101.midas.shareholders.dto.Shareholder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

public interface LockReader {

    @Transactional(readOnly = true)
    boolean isLocked(Integer shareholderId, Year year);

    @Transactional(readOnly = true)
    boolean isLocked(Shareholder shareholder, Year year);
}
