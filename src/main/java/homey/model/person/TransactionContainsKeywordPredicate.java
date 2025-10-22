package homey.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Locale;
import java.util.function.Predicate;

import homey.commons.util.ToStringBuilder;

public class TransactionContainsKeywordPredicate implements Predicate<Person>{
    private final String keywordLowerCased;

    public TransactionContainsKeywordPredicate(String keyword) {
        requireNonNull(keyword);
        this.keywordLowerCased = keyword.toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean test(Person person) {
        String transaction = person.getStage().value.toLowerCase(Locale.ROOT);
        return !transaction.isEmpty() && transaction.contains(keywordLowerCased);
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
