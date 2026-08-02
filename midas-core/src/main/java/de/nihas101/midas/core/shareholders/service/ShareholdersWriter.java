package de.nihas101.midas.core.shareholders.service;

import de.nihas101.midas.core.shareholders.dto.Shareholder;

public interface ShareholdersWriter {

    void create(final Shareholder shareholder);

    void update(final Shareholder shareholder);

    void delete(final Shareholder shareholder);
}
