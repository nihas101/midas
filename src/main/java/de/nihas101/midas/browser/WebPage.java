package de.nihas101.midas.browser;

import java.io.IOException;

public class WebPage {

    private final String url;

    public WebPage(String url) {
        this.url = url;
    }

    public void open() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();

        ProcessBuilder pb = null;

        if (os.contains("win")) {
            pb = new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url);
        } else if (os.contains("mac")) {
            pb = new ProcessBuilder("open", url);
        } else if (os.contains("nix") || os.contains("nux")) {
            pb = new ProcessBuilder("xdg-open", url);
        }

        if (pb != null) {
            pb.start();
        }
    }
}
