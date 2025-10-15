package homey.model.person;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Represents an optional meeting date-time for a person.
 * <p>Format: {@code yyyy-MM-dd HH:mm} (24-hour), strictly validated. Example: {@code 2025-11-03 14:00}.
 */
public class Meeting {

    public static final String MESSAGE_CONSTRAINTS =
            "Meeting must be in yyyy-MM-dd HH:mm (24h) format and be a real date/time, e.g. 2025-11-03 14:00.";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    private final LocalDateTime value;

    /**
     * Creates a Meeting from a raw string in {@code yyyy-MM-dd HH:mm} format.
     *
     * @throws IllegalArgumentException if the string is invalid.
     */
    public Meeting(String raw) {
        requireNonNull(raw);
        if (!isValidMeeting(raw)) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        this.value = LocalDateTime.parse(raw.trim(), FORMATTER);
    }

    /** Returns true if {@code test} is a valid meeting string. */
    public static boolean isValidMeeting(String test) {
        if (test == null) {
            return false;
        }
        String s = test.trim();
        if (s.isEmpty()) {
            return false;
        }
        try {
            LocalDateTime.parse(s, FORMATTER); // STRICT: validates month/day/hour/min ranges
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    /** Returns a display string */
    public String toDisplayString() {
        return value.format(FORMATTER);
    }

    @Override
    public String toString() {
        return value.format(FORMATTER);
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof Meeting && value.equals(((Meeting) other).value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
