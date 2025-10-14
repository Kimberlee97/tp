package homey.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import homey.testutil.PersonBuilder;

public class TagContainsKeywordsPredicateTest {
    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        TagContainsKeywordsPredicate firstPredicate = new TagContainsKeywordsPredicate(firstPredicateKeywordList);
        TagContainsKeywordsPredicate secondPredicate = new TagContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        TagContainsKeywordsPredicate firstPredicateCopy = new TagContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different predicate -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_tagContainsKeywords_returnsTrue() {
        // one keyword
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(Collections.singletonList("friend"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friend").build()));

        // multiple keywords
        predicate = new TagContainsKeywordsPredicate(Arrays.asList("friend", "colleague"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friend", "colleague").build()));

        // only one matching keyword
        predicate = new TagContainsKeywordsPredicate(Arrays.asList("friend", "family"));
        assertTrue(predicate.test(new PersonBuilder().withTags("colleague", "family").build()));

        // mixed-case keywords
        predicate = new TagContainsKeywordsPredicate(Arrays.asList("fRIeNd", "COLLeague"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friend", "colleague").build()));
    }

    @Test
    public void test_tagDoesNotContainKeywords_returnsFalse() {
        // zero keywords
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(new PersonBuilder().withTags("friend").build()));

        // non-matching keyword
        predicate = new TagContainsKeywordsPredicate(Arrays.asList("family"));
        assertFalse(predicate.test(new PersonBuilder().withTags("friend", "colleague").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(keywords);

        String expected = TagContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }

    @Test
    public void test_partialKeywordMatch_returnsTrue() {
        // partial match at start of tag
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(Collections.singletonList("fri"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friend").build()));

        // partial match at the end of tag
        predicate = new TagContainsKeywordsPredicate(Collections.singletonList("end"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friend").build()));

        // partial match in middle of tag
        predicate = new TagContainsKeywordsPredicate(Collections.singletonList("ien"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friend").build()));

        // partial match with mixed case
        predicate = new TagContainsKeywordsPredicate(Collections.singletonList("fRi"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friend").build()));

        // partial match in one of multiple tags
        predicate = new TagContainsKeywordsPredicate(Collections.singletonList("col"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friend", "colleague").build()));
    }

    @Test
    public void test_emptyKeyword_returnsFalse() {
        // empty string keyword should be ignored, non-empty keyword matches
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(Arrays.asList("", "friend"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friend", "colleague").build()));

        // only empty keywords
        predicate = new TagContainsKeywordsPredicate(Arrays.asList("", ""));
        assertFalse(predicate.test(new PersonBuilder().withTags("friend", "colleague").build()));
    }

    @Test
    public void test_noTags_returnsFalse() {
        // person has no tags
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(Collections.singletonList("friend"));
        assertFalse(predicate.test(new PersonBuilder().build()));

        // multiple keywords but person has no tags
        predicate = new TagContainsKeywordsPredicate(Arrays.asList("friend", "colleague"));
        assertFalse(predicate.test(new PersonBuilder().build()));
    }
}
