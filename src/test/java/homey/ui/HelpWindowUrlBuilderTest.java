package homey.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class HelpWindowUrlBuilderTest {

    @Test
    public void knownTopic_add_usesAnchor() {
        String expected = HelpWindow.USERGUIDE_URL + "#adding-a-contact-add";
        assertEquals(expected, HelpWindow.buildUserGuideUrl("add"));
    }

    @Test
    public void nullTopic_returnsRootUrl() {
        assertEquals(HelpWindow.USERGUIDE_URL, HelpWindow.buildUserGuideUrl(null));
    }

    @Test
    public void unknownTopic_returnsRootUrl() {
        assertEquals(HelpWindow.USERGUIDE_URL, HelpWindow.buildUserGuideUrl("does-not-exist"));
    }

    @Test
    public void knownTopic_help_usesAnchor() {
        String expected = HelpWindow.USERGUIDE_URL + "#viewing-help";
        assertEquals(expected, HelpWindow.buildUserGuideUrl("help"));
    }

    @Test
    public void emptyString_returnsRootUrl() {
        assertEquals(HelpWindow.USERGUIDE_URL, HelpWindow.buildUserGuideUrl(""));
    }

    @Test
    void mixedCaseAndSpaces_normalizedByBuilder_returnsAnchor() {
        String raw = "  AdD  ";
        String expected = HelpWindow.USERGUIDE_URL + "#adding-a-contact-add";
        assertEquals(expected, HelpWindow.buildUserGuideUrl(raw));
    }
}
