package homey.model.tag;

import static homey.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents the transaction stage between the person and the addressbook owner.
 */
public class TransactionStage {
    public static final String MESSAGE_CONSTRAINTS = "Transaction stage should be 'prospect', 'negotiating'"
            + " or 'closed'.";
    public static final String[] VALID_STAGES = new String[]{"prospect", "negotiating", "closed"};
    public static final String MESSAGE_ARGUMENTS = "Index = %1$d, Stage = %2$s";
    public final String value;

    /**
     * Constructs a {@code TransactionStage}
     * @param stageName A valid stage name.
     */
    public TransactionStage(String stageName) {
        requireNonNull(stageName);
        String normalisedStage = stageName.trim().toLowerCase();
        checkArgument(isValid(normalisedStage), MESSAGE_CONSTRAINTS);
        this.value = normalisedStage;
    }

    /**
     * Returns true if the given string is a valid stage name (case-insensitive).
     * @param test
     * @return
     */
    public static boolean isValid(String test) {
        requireNonNull(test);
        String normalisedStage = test.trim().toLowerCase(); // defensive normalization
        for (String validStage : VALID_STAGES) {
            if (normalisedStage.equals(validStage)) {
                return true;
            }
        }
        return false;
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
