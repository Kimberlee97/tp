package homey.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Tests that a {@code Person}'s {@code Address} contains ANY of the given keywords (case-insensitive).
 * Will pass a single keyword, but this supports multiple (future-proof).
 */
public class AddressContainsKeywordsPredicate implements Predicate<Person> {

    private final List<String> keywordsLowerCased;

    public AddressContainsKeywordsPredicate(List<String> keywords) {
        requireNonNull(keywords);
        this.keywordsLowerCased = keywords.stream()
                .map(k -> k == null ? "" : k.toLowerCase(Locale.ROOT))
                .toList();
    }

    @Override
    public boolean test(Person person) {
        String addr = person.getAddress().value.toLowerCase(Locale.ROOT);
        for (String k : keywordsLowerCased) {
            if (!k.isEmpty() && addr.contains(k)) {
                return true; // ANY-match
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof AddressContainsKeywordsPredicate)
                && keywordsLowerCased.equals(((AddressContainsKeywordsPredicate) other).keywordsLowerCased);
    }

    @Override
    public int hashCode() {
        return keywordsLowerCased.hashCode();
    }

    @Override
    public String toString() {
        return "address contains " + String.join(", ", keywordsLowerCased);
    }
}

