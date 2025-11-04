package homey.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HelpWindowOpenHelperTest {

    @Test
    @DisplayName("opener true -> uses correct anchor URL; fallback not called")
    void open_success_noFallback() {
        AtomicReference<String> seenUrl = new AtomicReference<>();
        AtomicBoolean ranFallback = new AtomicBoolean(false);

        HelpWindow.openInBrowserOrShow(
                "add",
                url -> {
                    seenUrl.set(url);
                    return true;
                }, () -> {
                    ranFallback.set(true);
                }
        );

        assertEquals(HelpWindow.USERGUIDE_URL + "#adding-a-contact-add", seenUrl.get());
        assertTrue(!ranFallback.get());
    }

    @Test
    @DisplayName("opener false -> fallback called")
    void open_failure_runsFallback() {
        AtomicBoolean ranFallback = new AtomicBoolean(false);

        HelpWindow.openInBrowserOrShow(
                "help",
                url -> false, () -> ranFallback.set(true)
        );

        assertTrue(ranFallback.get(), "fallback should run when opener fails");
    }

    @Test
    @DisplayName("null topic -> root User Guide URL passed to opener")
    void open_nullTopic_rootUrl() {
        AtomicReference<String> seenUrl = new AtomicReference<>();

        HelpWindow.openInBrowserOrShow(
                null,
                url -> {
                    seenUrl.set(url);
                    return true;
                }, () -> {
                }
        );

        assertEquals(HelpWindow.USERGUIDE_URL, seenUrl.get());
    }
}
