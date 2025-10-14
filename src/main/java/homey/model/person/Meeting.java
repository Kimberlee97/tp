package homey.model.person;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Represents an optional meeting date and time for a {@code Person}.
 * This class is immutable and uses ISO 8601 format: {@code yyyy-MM-dd HH:mm}.
 */
public class Meeting {

    public static final String MESSAGE_CONSTRAINTS =
            "Meetings should follow the format 'yyyy-MM-dd HH:mm', e.g., 2025-11-01 14:00";

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final LocalDateTime dateTime;

    /**
     * Constructs a {@code Meeting} from a valid {@link LocalDateTime}.
     *
     * @param dateTime The meeting date and time.
     */
    public Meeting(LocalDateTime dateTime) {
        requireNonNull(dateTime);
        this.dateTime = dateTime;
    }

    /**
     * Parses and constructs a {@code Meeting} from the given date-time string.
     *
     * @param dateTimeStr The string representation of the meeting date-time.
     * @throws IllegalArgumentException If the string does not match the required format.
     */
    public Meeting(String dateTimeStr) {
        requireNonNull(dateTimeStr);
        try {
            this.dateTime = LocalDateTime.parse(dateTimeStr.trim(), FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
    }

    /**
     * Returns {@code true} if the given string is a valid meeting date-time.
     */
    public static boolean isValidMeeting(String test) {
        try {
            LocalDateTime.parse(test.trim(), FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns the meeting time formatted for display.
     */
    public String toDisplayString() {
        return dateTime.format(FORMATTER);
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return toDisplayString();
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof Meeting)
                && dateTime.equals(((Meeting) other).dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime);
    }
}

