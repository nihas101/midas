package de.nihas101.midas.api.shareholder;

public interface ShareholdersWriter {

    void create(final Shareholder shareholder);

    void update(final Shareholder shareholder);

    void delete(final Shareholder shareholder);
}
