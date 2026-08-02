package de.nihas101.midas.api.export;

public interface Export {

    void trigger();

    String fileName();

    String mimeType();
}
