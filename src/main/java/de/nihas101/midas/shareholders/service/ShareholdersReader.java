package de.nihas101.midas.shareholders.service;

import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.dto.Shareholders;

public interface ShareholdersReader {

    Shareholder shareholder(final int shareholderId);

    Shareholders shareholders();
}
