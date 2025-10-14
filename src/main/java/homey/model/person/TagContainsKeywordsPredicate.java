package homey.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import homey.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s tags contain any of the specified keywords.
 * Keywords are matched case-insensitively and as substrings within tag names.
 */
public class TagContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywordsLowerCased;

    /**
     * Constructs a {@code TagContainsKeywordsPredicate} with the given keywords.
     * Keywords are converted to lowercase for case-insensitive matching.
     *
     * @param keywords The list of keywords to search for in tags. Must not be null.
     * @throws NullPointerException if {@code keywords} is null.
     */
    public TagContainsKeywordsPredicate(List<String> keywords) {
        requireNonNull(keywords);
        this.keywordsLowerCased = keywords.stream()
                .map(k -> k == null ? "" : k.toLowerCase(Locale.ROOT))
                .toList();
    }


    @Override
    public boolean test(Person person) {
        boolean anyMatch = person.getTags().stream()
                .map(t -> t.tagName.toLowerCase(Locale.ROOT))
                .anyMatch(tagName -> keywordsLowerCased.stream()
                        .anyMatch(k -> !k.isEmpty() && tagName.contains(k)));
        return anyMatch;
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof TagContainsKeywordsPredicate)
                && keywordsLowerCased.equals(((TagContainsKeywordsPredicate) other).keywordsLowerCased);

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
