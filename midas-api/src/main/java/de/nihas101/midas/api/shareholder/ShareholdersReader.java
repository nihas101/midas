package de.nihas101.midas.api.shareholder;

public interface ShareholdersReader {

    Shareholder shareholder(final int shareholderId);

    Shareholders shareholders();

    Shareholders shareholders(final Iterable<Integer> shareholderIds);
}
