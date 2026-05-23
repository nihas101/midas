package de.nihas101.midas.export.pdf;

public interface PdfGenerator {
    void generate();

    String fileName();

    String mimeType();
}
