package homey.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Locale;
import java.util.function.Predicate;

import homey.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Stage} matches the given keyword.
 * The keyword must match exactly (case-insensitive) with the person's transaction stage
 */
public class TransactionContainsKeywordPredicate implements Predicate<Person> {
    private final String keywordLowerCased;

    /**
     * Constructs a {@code TransactionContainsKeywordPredicate} with the given keyword.
     * @param keyword must match against the person's tramsaction stage.
     *                Must not be null.
     */
    public TransactionContainsKeywordPredicate(String keyword) {
        requireNonNull(keyword);
        this.keywordLowerCased = keyword.toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean test(Person person) {
        String transaction = person.getStage().value.toLowerCase(Locale.ROOT);
        return !transaction.isEmpty() && transaction.equals(keywordLowerCased);
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof TransactionContainsKeywordPredicate)
                && keywordLowerCased.equals(((TransactionContainsKeywordPredicate) other).keywordLowerCased);
    }

    @Override
    public int hashCode() {
        return keywordLowerCased.hashCode();
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keyword", keywordLowerCased).toString();
    }
}
