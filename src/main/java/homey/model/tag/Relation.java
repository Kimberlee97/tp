package homey.model.tag;

import static homey.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

public class Relation {
    public static final String MESSAGE_CONSTRAINTS = "Relation should be 'client' or 'vendor'.";

    public final String relationName;

    /**
     * Constructs a {@code Relation}.
     *
     * @param relationName A valid relation.
     */
    public Relation(String relationName) {
        requireNonNull(relationName);
        checkArgument(isValidRelation(relationName), MESSAGE_CONSTRAINTS);
        this.relationName = relationName;
    }

    /**
     * Returns true if a given string is a valid relation.
     */
    public static boolean isValidRelation(String test) {
        return test.equals("client") || test.equals("vendor");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Relation)) {
            return false;
        }

        Relation otherRelation = (Relation) other;
        return relationName.equals(otherRelation.relationName);
    }

    @Override
    public int hashCode() {
        return relationName.hashCode();
    }

    /**
     * Format state as text for viewing.
     */
    public String toString() {
        return '[' + relationName + ']';
    }
}
