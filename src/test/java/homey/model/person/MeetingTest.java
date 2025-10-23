package homey.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class MeetingTest {

    @Test
    void isValidMeeting_validFormats_true() {
        assertTrue(Meeting.isValidMeeting("2025-11-03 14:00"));
        assertTrue(Meeting.isValidMeeting("1999-01-01 00:00"));
        assertTrue(Meeting.isValidMeeting("2030-12-31 23:59"));
    }

    @Test
    void isValidMeeting_invalidFormats_false() {
        // wrong order / separators / impossible month/day/time
        assertFalse(Meeting.isValidMeeting("03-11-2025 14:00"));
        assertFalse(Meeting.isValidMeeting("2025/11/03 14:00"));
        assertFalse(Meeting.isValidMeeting("2025-13-03 14:00"));
        assertFalse(Meeting.isValidMeeting("2025-11-32 14:00"));
        assertFalse(Meeting.isValidMeeting("2025-11-03 24:00"));
        assertFalse(Meeting.isValidMeeting("2025-11-03 23:60"));
        assertFalse(Meeting.isValidMeeting("nonsense"));
        assertFalse(Meeting.isValidMeeting(""));
        assertFalse(Meeting.isValidMeeting("   "));
    }

    @Test
    void constructor_parsesToString_roundtrip() {
        String raw = "2025-11-03 14:00";
        Meeting m = new Meeting(raw);
        // toString() should match the raw normalized format
        assertEquals(raw, m.toString());
        // toDisplayString should contain both date and time in same pattern by default
        assertTrue(m.toDisplayString().contains("2025-11-03"));
        assertTrue(m.toDisplayString().contains("14:00"));
    }

    @Test
    void equalsAndHashCode() {
        Meeting a = new Meeting("2025-11-03 14:00");
        Meeting b = new Meeting("2025-11-03 14:00");
        Meeting c = new Meeting("2025-11-04 09:00");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertFalse(a.equals(null));
        assertFalse(a.equals(c));
    }

    @Test
    void constructor_invalid_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Meeting("2025-99-99 99:99"));
        assertThrows(IllegalArgumentException.class, () -> new Meeting(" "));
        assertThrows(IllegalArgumentException.class, () -> new Meeting("abc"));
    }

    @Test
    public void getDateTime_returnsParsedLocalDateTime() {
        Meeting mtg = new Meeting("2025-12-01 09:00");
        LocalDateTime expected = LocalDateTime.of(2025, 12, 1, 9, 0);
        assertEquals(expected, mtg.getDateTime());
    }

    @Test
    public void updateOverdueStatus() {
        Meeting meeting = new Meeting("2023-10-20 14:00");
        LocalDateTime now = LocalDateTime.of(2023, 10, 21, 14, 0);
        meeting.updateOverdueStatus();
        assertTrue(Meeting.isOverdueMeeting(meeting));

        meeting = new Meeting("3000-10-22 14:00");
        meeting.updateOverdueStatus();
        assertFalse(Meeting.isOverdueMeeting(meeting));
    }
}
