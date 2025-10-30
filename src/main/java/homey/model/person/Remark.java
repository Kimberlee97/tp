package homey.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's remark in the address book.
 * Guarantees: immutable; is always valid
 */
public class Remark {

    public final String value;

    /**
     * Creates a new {@code Remark} object.
     * @param remark the given remark.
     */
    public Remark(String remark) {
        requireNonNull(remark);
        String trimmed = remark.trim();
        if (trimmed.length() > 100) {
            throw new IllegalArgumentException("Remark cannot exceed 100 characters.");
        }
        value = trimmed;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof Remark && value.equals(((Remark) other).value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
