package de.nihas101.midas.api.shareholder;

public interface ShareholdersReader {

    Shareholder shareholder(final int shareholderId);

    Shareholders shareholders();
}
