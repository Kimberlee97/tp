package homey.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Tests that a {@code Person}'s {@code Address} matches the given query.
 * If phraseMode == false: match if address contains ANY keyword (case-insensitive).
 * If phraseMode == true : treat the first element as a single contiguous phrase.
 */
public class AddressContainsKeywordsPredicate implements Predicate<Person> {

    private final List<String> keywordsLowerCased;
    private final boolean phraseMode;

    /**
     * Creates a predicate that checks if a person's address contains
     * any of the given keywords, ignoring case.
     *
     * @param keywords list of keywords to look for in the address
     */
    public AddressContainsKeywordsPredicate(List<String> keywords) {
        this(keywords, false);
    }

    /**
     * Creates a predicate that checks if a person's address matches
     * either a list of keywords (OR search) or a single phrase.
     *
     * @param keywords list of keywords or a single phrase
     * @param phraseMode true to treat the first element as a phrase, false for normal keyword search
     */
    public AddressContainsKeywordsPredicate(List<String> keywords, boolean phraseMode) {
        requireNonNull(keywords);
        this.keywordsLowerCased = keywords.stream()
                .map(k -> k == null ? "" : k.toLowerCase(Locale.ROOT))
                .toList();
        this.phraseMode = phraseMode;
    }

    @Override
    public boolean test(Person person) {
        String addr = person.getAddress().value.toLowerCase(Locale.ROOT);
        if (phraseMode) {
            String phrase = keywordsLowerCased.isEmpty() ? "" : keywordsLowerCased.get(0);
            return !phrase.isEmpty() && addr.contains(phrase);
        }
        for (String k : keywordsLowerCased) {
            if (!k.isEmpty() && addr.contains(k)) {
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
        if (!(other instanceof AddressContainsKeywordsPredicate)) {
            return false;
        }
        AddressContainsKeywordsPredicate o = (AddressContainsKeywordsPredicate) other;
        return phraseMode == o.phraseMode
                && keywordsLowerCased.equals(o.keywordsLowerCased);
    }

    @Override
    public int hashCode() {
        int h = keywordsLowerCased.hashCode();
        return 31 * h + (phraseMode ? 1 : 0);
    }

    @Override
    public String toString() {
        return (phraseMode ? "address contains phrase " : "address contains ")
                + String.join(", ", keywordsLowerCased);
    }
}
