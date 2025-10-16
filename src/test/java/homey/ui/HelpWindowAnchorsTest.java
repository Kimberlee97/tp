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

        assertEquals("#adding-a-person-add", anchors.get("add"));
        assertEquals("#editing-a-person-edit", anchors.get("edit"));
        assertEquals("#deleting-a-person-delete", anchors.get("delete"));
        assertEquals("#locating-persons-by-name-find", anchors.get("find"));
        assertEquals("#listing-all-persons-list", anchors.get("list"));
        assertEquals("#viewing-help-help-topic", anchors.get("help"));
    }
}
