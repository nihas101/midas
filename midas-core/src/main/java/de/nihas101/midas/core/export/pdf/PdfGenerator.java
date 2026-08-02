package de.nihas101.midas.core.export.pdf;

public interface PdfGenerator {
    void generate();

    String fileName();

    String mimeType();
}
