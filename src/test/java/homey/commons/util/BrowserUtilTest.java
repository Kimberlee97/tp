package homey.commons.util;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class BrowserUtilTest {

    @Test
    void open_returnsFalse_inHeadless() {
        String prev = System.getProperty("java.awt.headless");
        System.setProperty("java.awt.headless", "true");
        try {
            assertFalse(BrowserUtil.open("https://example.com"));
        } finally {
            if (prev != null) {
                System.setProperty("java.awt.headless", prev);
            } else {
                System.clearProperty("java.awt.headless");
            }
        }
    }
}
