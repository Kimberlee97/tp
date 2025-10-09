package homey.model.tag;

import static homey.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents the transaction stage between the person and the addressbook owner.
 */
public class TransactionStage {
    public static final String MESSAGE_CONSTRAINTS = "Transaction stage should be 'prospect', 'negotiating'"
            + "or 'closed'.";

    public final String value;

    /**
     * Constructs a {@code TransactionStage}
     * @param stageName A valid stage name.
     */
    public TransactionStage(String stageName) {
        requireNonNull(stageName);
        checkArgument(isValid(stageName), MESSAGE_CONSTRAINTS);
        this.value = stageName;
    }

    /**
     * Returns true if the given string is a valid stage name.
     * @param test
     * @return
     */
    public static boolean isValid(String test) {
        return test.equals("prospect") || test.equals("negotiating") || test.equals("closed");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TransactionStage)) {
            return false;
        }

        return value.equals(((TransactionStage) other).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public String toString() {
        return '[' + value + ']';
    }
}
