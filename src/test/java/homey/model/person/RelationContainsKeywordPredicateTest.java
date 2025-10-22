package homey.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import homey.testutil.PersonBuilder;

public class RelationContainsKeywordPredicateTest {
    @Test
    public void equals() {
        RelationContainsKeywordPredicate firstPredicate = new RelationContainsKeywordPredicate("client");
        RelationContainsKeywordPredicate secondPredicate = new RelationContainsKeywordPredicate("vendor");

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        RelationContainsKeywordPredicate firstPredicateCopy = new RelationContainsKeywordPredicate("client");
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different predicate -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_relationMatchesKeyword_returnsTrue() {
        // exact match - client
        RelationContainsKeywordPredicate predicate = new RelationContainsKeywordPredicate("client");
        assertTrue(predicate.test(new PersonBuilder().withRelation("client").build()));

        // exact match - vendor
        predicate = new RelationContainsKeywordPredicate("vendor");
        assertTrue(predicate.test(new PersonBuilder().withRelation("vendor").build()));

        // mixed-case keyword
        predicate = new RelationContainsKeywordPredicate("ClIeNt");
        assertTrue(predicate.test(new PersonBuilder().withRelation("client").build()));
    }

    @Test
    public void test_relationDoesNotMatchKeyword_returnsFalse() {
        // non-matching keyword
        RelationContainsKeywordPredicate predicate = new RelationContainsKeywordPredicate("client");
        assertFalse(predicate.test(new PersonBuilder().withRelation("vendor").build()));

        // partial match at start
        predicate = new RelationContainsKeywordPredicate("cli");
        assertFalse(predicate.test(new PersonBuilder().withRelation("client").build()));

        // partial match at end
        predicate = new RelationContainsKeywordPredicate("ent");
        assertFalse(predicate.test(new PersonBuilder().withRelation("client").build()));

        // partial match in middle
        predicate = new RelationContainsKeywordPredicate("lie");
        assertFalse(predicate.test(new PersonBuilder().withRelation("client").build()));

        // keyword contains relation
        predicate = new RelationContainsKeywordPredicate("clients");
        assertFalse(predicate.test(new PersonBuilder().withRelation("client").build()));
    }

    @Test
    public void test_emptyKeyword_returnsFalse() {
        // empty keyword should not match valid relation
        RelationContainsKeywordPredicate predicate = new RelationContainsKeywordPredicate("");
        assertFalse(predicate.test(new PersonBuilder().withRelation("client").build()));
    }

    @Test
    public void toStringMethod() {
        String keyword = "client";
        RelationContainsKeywordPredicate predicate = new RelationContainsKeywordPredicate(keyword);

        String expected = RelationContainsKeywordPredicate.class.getCanonicalName()
                + "{keyword=" + keyword.toLowerCase() + "}";
        assertEquals(expected, predicate.toString());
    }

    @Test
    public void test_whitespaceHandling() {
        // keyword with extra whitespace should not match valid relation
        RelationContainsKeywordPredicate predicate = new RelationContainsKeywordPredicate("cli ent");
        assertFalse(predicate.test(new PersonBuilder().withRelation("client").build()));

        // keyword with leading/trailing whitespace should not match
        predicate = new RelationContainsKeywordPredicate(" vendor ");
        assertFalse(predicate.test(new PersonBuilder().withRelation("vendor").build()));
    }
}
