package de.nihas101.midas.api.shareholder;

import de.nihas101.midas.api.DeleteMode;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ShareholdersWriter {

    void create(final Shareholder shareholder);

    void update(final Shareholder shareholder);

    void delete(final Shareholder shareholder, final DeleteMode deleteMode);

}
