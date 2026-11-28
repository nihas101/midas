package de.nihas101.midas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MidasApplicationTest {

    @Test
    void resolveLaunchBrowser_default_returnsTrue() {
        boolean launchBrowser = MidasApplication.resolveLaunchBrowser(new String[]{});
        assertTrue(launchBrowser);
    }

    @Test
    void resolveLaunchBrowser_cliArgFalse_returnsFalse() {
        boolean launchBrowser = MidasApplication.resolveLaunchBrowser(new String[]{"--desktop.launch-browser=false"});
        assertFalse(launchBrowser);
    }

    @Test
    void resolveLaunchBrowser_cliArgTrue_returnsTrue() {
        boolean launchBrowser = MidasApplication.resolveLaunchBrowser(new String[]{"--desktop.launch-browser=true"});
        assertTrue(launchBrowser);
    }

    @Test
    void resolveLaunchBrowser_camelCaseCliArg_returnsFalse() {
        boolean launchBrowser = MidasApplication.resolveLaunchBrowser(new String[]{"--desktop.launchBrowser=false"});
        assertFalse(launchBrowser);
    }
}
