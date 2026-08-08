package de.nihas101.midas.api.lock;

import java.time.Year;

public interface Lock {

    Integer getId();

    Integer getShareholderId();

    Year getYear();
}
