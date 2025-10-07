package homey.commons.util;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class BrowserUtilTest {

    @Test
    void open_returnsFalse_inHeadless() {
        System.setProperty("java.awt.headless", "true");
        assertFalse(BrowserUtil.open("https://example.com"));
    }
}
