package homey.commons.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/**
 * Utility class to open a URL in the system's default web browser.
 */
public final class BrowserUtil {
    private BrowserUtil() {}

    /**
     * Tries to open the given URL in the system's default browser.
     *
     * @param url The URL to open.
     * @return true if the browser was opened successfully, false otherwise.
     */
    public static boolean open(String url) {
        try {
            if (!Desktop.isDesktopSupported()) {
                return false;
            }
            Desktop desktop = Desktop.getDesktop();
            if (!desktop.isSupported(Desktop.Action.BROWSE)) {
                return false;
            }
            desktop.browse(URI.create(url));
            return true;
        } catch (IOException | IllegalArgumentException e) {
            return false;
        }
    }
}
