package homey.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import homey.commons.util.ToStringBuilder;


/**
 * Tests that a {@code Person}'s {@code Name} matches any of the keywords given.
 * Matching is case-insensitive and allows partial keyword matches
 */
public class NameContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywordsLowerCased;

    /**
     * Constructs a NameContainsKeywordsPredicate with the given list of keywords.
     * All keywords are converted to lowercase for case-insensitive matching.
     * @param keywords the list of keywords to match against; must not be null
     * @throws NullPointerException if keywords is null
     */
    public NameContainsKeywordsPredicate(List<String> keywords) {
        requireNonNull(keywords);
        this.keywordsLowerCased = keywords.stream()
                .map(k -> k == null ? "" : k.toLowerCase(Locale.ROOT))
                .toList();
    }

    @Override
    public boolean test(Person person) {
        String name = person.getName().fullName.toLowerCase(Locale.ROOT);
        for (String k : keywordsLowerCased) {
            if (!k.isEmpty() && name.contains(k)) {
                return true; // match partial keywords
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof NameContainsKeywordsPredicate)
                && keywordsLowerCased.equals(((NameContainsKeywordsPredicate) other).keywordsLowerCased);

    }

    @Override
    public int hashCode() {
        return keywordsLowerCased.hashCode();
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywordsLowerCased).toString();
    }
}
