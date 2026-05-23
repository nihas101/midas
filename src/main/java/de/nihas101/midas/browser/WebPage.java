package de.nihas101.midas.browser;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class WebPage {

    private final String url;

    public WebPage(String url) {
        this.url = url;
    }

    public void open() throws IOException {
        final String osName = System.getProperty("os.name");
        if (StringUtils.isBlank(osName)) {
            throw new RuntimeException("Failed to determine operating system");
        }

        String os = osName.toLowerCase();

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
