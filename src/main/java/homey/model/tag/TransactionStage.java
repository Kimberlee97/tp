package homey.model.tag;

import static homey.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

public class TransactionStage {
    public static final String MESSAGE_CONSTRAINTS = "Transaction stage should be 'prospect', 'negotiating'"
            + "or 'closed'.";

    public final String value;

    public TransactionStage(String stageName) {
        requireNonNull(stageName);
        checkArgument(isValid(stageName), MESSAGE_CONSTRAINTS);
        this.value = stageName;
    }

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
