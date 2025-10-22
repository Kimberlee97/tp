package homey.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import homey.testutil.PersonBuilder;

public class TransactionContainsKeywordPredicateTest {
    @Test
    public void equals() {
        TransactionContainsKeywordPredicate firstPredicate = new TransactionContainsKeywordPredicate("prospect");
        TransactionContainsKeywordPredicate secondPredicate = new TransactionContainsKeywordPredicate("negotiating");

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        TransactionContainsKeywordPredicate firstPredicateCopy = new TransactionContainsKeywordPredicate("prospect");
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different predicate -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_transactionMatchesKeyword_returnsTrue() {
        // exact match - prospect
        TransactionContainsKeywordPredicate predicate = new TransactionContainsKeywordPredicate("prospect");
        assertTrue(predicate.test(new PersonBuilder().withStage("prospect").build()));

        // exact match - negotiating
        predicate = new TransactionContainsKeywordPredicate("negotiating");
        assertTrue(predicate.test(new PersonBuilder().withStage("negotiating").build()));

        // exact match - closed
        predicate = new TransactionContainsKeywordPredicate("closed");
        assertTrue(predicate.test(new PersonBuilder().withStage("closed").build()));

        // mixed-case keyword
        predicate = new TransactionContainsKeywordPredicate("NeGoTiAtInG");
        assertTrue(predicate.test(new PersonBuilder().withStage("negotiating").build()));

    }

    @Test
    public void test_transactionDoesNotMatchKeyword_returnsFalse() {
        // non-matching keyword
        TransactionContainsKeywordPredicate predicate = new TransactionContainsKeywordPredicate("prospect");
        assertFalse(predicate.test(new PersonBuilder().withStage("negotiating").build()));

        // partial match at start
        predicate = new TransactionContainsKeywordPredicate("neg");
        assertFalse(predicate.test(new PersonBuilder().withStage("negotiating").build()));

        // partial match at end
        predicate = new TransactionContainsKeywordPredicate("ating");
        assertFalse(predicate.test(new PersonBuilder().withStage("negotiating").build()));

        // partial match in middle
        predicate = new TransactionContainsKeywordPredicate("gotiat");
        assertFalse(predicate.test(new PersonBuilder().withStage("negotiating").build()));

        // keyword contains stage
        predicate = new TransactionContainsKeywordPredicate("closedness");
        assertFalse(predicate.test(new PersonBuilder().withStage("closed").build()));
    }

    @Test
    public void test_emptyKeyword_returnsFalse() {
        // empty keyword should not match valid stage
        TransactionContainsKeywordPredicate predicate = new TransactionContainsKeywordPredicate("");
        assertFalse(predicate.test(new PersonBuilder().withStage("prospect").build()));
    }

    @Test
    public void toStringMethod() {
        String keyword = "negotiating";
        TransactionContainsKeywordPredicate predicate = new TransactionContainsKeywordPredicate(keyword);

        String expected = TransactionContainsKeywordPredicate.class.getCanonicalName()
                + "{keyword=" + keyword.toLowerCase() + "}";
        assertEquals(expected, predicate.toString());
    }

    @Test
    public void test_whitespaceHandling() {
        // keyword with extra whitespace should not match valid stage
        TransactionContainsKeywordPredicate predicate = new TransactionContainsKeywordPredicate("nego tiating");
        assertFalse(predicate.test(new PersonBuilder().withStage("negotiating").build()));

        // keyword with leading/trailing whitespace should not match
        predicate = new TransactionContainsKeywordPredicate(" closed ");
        assertFalse(predicate.test(new PersonBuilder().withStage("closed").build()));
    }
}
