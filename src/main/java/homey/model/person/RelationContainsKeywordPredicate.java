package homey.model.person;

import static homey.model.tag.Relation.isValidRelation;
import static java.util.Objects.requireNonNull;

import java.util.Locale;
import java.util.function.Predicate;

import homey.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Relation} matches the keyword given.
 * Matching is case-insensitive and does not allow partial keyword matches.
 */
public class RelationContainsKeywordPredicate implements Predicate<Person> {
    private final String keywordLowerCased;

    /**
     * Constructs a RelationContainsKeywordPredicate with the given keyword.
     * The keyword is converted to lowercase for case-insensitive matching.
     * @param keyword the keyword to match against; must not be null
     * @throws NullPointerException if the keyword is null
     */
    public RelationContainsKeywordPredicate(String keyword) {
        requireNonNull(keyword);
        this.keywordLowerCased = keyword.toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean test(Person person) {
        String relation = person.getRelation().value.toLowerCase(Locale.ROOT);
        return !keywordLowerCased.isEmpty() && isValidRelation(relation) && keywordLowerCased.equals(relation);
    }

    @Override
    public boolean equals (Object other) {
        return other == this
                || (other instanceof RelationContainsKeywordPredicate)
                && keywordLowerCased.equals(((RelationContainsKeywordPredicate) other).keywordLowerCased);
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
