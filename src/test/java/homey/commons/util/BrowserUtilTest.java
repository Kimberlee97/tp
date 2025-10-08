package homey.commons.util;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Assumptions;
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

    @Test
    void constructor_isPrivate_andReachableViaReflection() throws Exception {
        var ctor = BrowserUtil.class.getDeclaredConstructor();
        // should be private
        assertFalse(ctor.canAccess(null));
        ctor.setAccessible(true);
        Object instance = ctor.newInstance();
        org.junit.jupiter.api.Assertions.assertNotNull(instance);
    }


    @Test
    void open_invalidUrl_returnsFalse() {
        // If Desktop is totally unsupported on the machine, this will early-return false anyway.
        // That's OK; either way the result is false, and the branch is covered in common CI setups.
        Assumptions.assumeTrue(java.awt.Desktop.isDesktopSupported() || true);
        assertFalse(BrowserUtil.open("ht!tp:// bad url"));
    }

    @Test
    void open_badStringInHeadless_returnsFalse() {
        String prev = System.getProperty("java.awt.headless");
        System.setProperty("java.awt.headless", "true");
        try {
            // still returns false via the headless short-circuit
            org.junit.jupiter.api.Assertions.assertFalse(BrowserUtil.open("not a url"));
        } finally {
            if (prev != null) {
                System.setProperty("java.awt.headless", prev);
            } else {
                System.clearProperty("java.awt.headless");
            }
        }
    }

    @Test
    void open_returnsFalse_whenBrowseActionNotSupported() {
        Assumptions.assumeTrue(java.awt.Desktop.isDesktopSupported(),
                "Desktop unsupported here; skip this branch");
        var desktop = java.awt.Desktop.getDesktop();
        Assumptions.assumeTrue(!desktop.isSupported(java.awt.Desktop.Action.BROWSE),
                "BROWSE is supported here; skip this branch");

        assertFalse(BrowserUtil.open("https://example.com"));
    }
}
