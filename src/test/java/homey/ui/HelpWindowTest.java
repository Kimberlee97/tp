package homey.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;

public class HelpWindowTest {

    @BeforeAll
    static void initFx() {
        System.setProperty("java.awt.headless", "true");
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignore) {
            // FX already started
        }
    }

    @Test
    public void openInBrowserOrShow_fallbackShowsWindow() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                HelpWindow hw = new HelpWindow();
                hw.openInBrowserOrShow("add");
                assertTrue(hw.isShowing(), "Help window should be visible when browser cannot open");
                hw.hide();
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(3, TimeUnit.SECONDS), "Timed out waiting for FX thread");
    }
}
