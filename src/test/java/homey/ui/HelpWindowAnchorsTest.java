package homey.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class HelpWindowAnchorsTest {

    @SuppressWarnings("unchecked")
    @Test
    public void anchors_containsExpectedEntries() throws Exception {
        Field f = HelpWindow.class.getDeclaredField("ANCHORS");
        f.setAccessible(true);
        Map<String, String> anchors = (Map<String, String>) f.get(null);

        assertEquals("#adding-a-contact-add", anchors.get("add"));
        assertEquals("#editing-your-contacts", anchors.get("edit"));
        assertEquals("#deleting-a-contact-delete", anchors.get("delete"));
        assertEquals("#finding-your-contacts", anchors.get("find"));
        assertEquals("#listing-your-contacts", anchors.get("list"));
        assertEquals("#viewing-help", anchors.get("help"));
        assertEquals("#adding-a-relation-relation", anchors.get("relation"));
        assertEquals("#tracking-deal-progress", anchors.get("transaction"));
        assertEquals("#find-by-tag-find-t", anchors.get("find t/"));
        assertEquals("#find-by-address-find-a", anchors.get("find a/"));
        assertEquals("#clearing-all-entries-clear", anchors.get("clear"));
        assertEquals("#exiting-the-program-exit", anchors.get("exit"));
    }
}
