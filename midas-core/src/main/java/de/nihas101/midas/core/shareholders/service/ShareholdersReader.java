package de.nihas101.midas.core.shareholders.service;

import de.nihas101.midas.core.shareholders.dto.Shareholder;
import de.nihas101.midas.core.shareholders.dto.Shareholders;

public interface ShareholdersReader {

    Shareholder shareholder(final int shareholderId);

    Shareholders shareholders();
}
