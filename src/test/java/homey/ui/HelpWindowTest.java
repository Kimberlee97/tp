package homey.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;

public class HelpWindowTest {

    @BeforeAll
    static void initFx() throws Exception {
        System.setProperty("java.awt.headless", "true");
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException alreadyStarted) {
            // OK: FX already initialised
        }
    }

    @Test
    void openInBrowserOrShow_fallbackShowsWindow() throws Exception {
        HelpWindow hw = new HelpWindow();

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                hw.openInBrowserOrShow("add");
            } finally {
                latch.countDown();
            }
        });
        latch.await(2, TimeUnit.SECONDS);

        assertTrue(hw.isShowing(), "Help window should be visible when browser cannot open");
        Platform.runLater(hw::hide);
    }
}
