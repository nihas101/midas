package de.nihas101.midas.core.export;

public interface Export {

    void trigger();

    String fileName();

    String mimeType();
}
