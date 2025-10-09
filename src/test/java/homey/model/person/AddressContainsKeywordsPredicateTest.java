package homey.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import homey.model.tag.TransactionStage;

import homey.model.tag.Relation;

public class AddressContainsKeywordsPredicateTest {

    private static Person personWith(String address) {

        return new Person(new Name("Alex Yeoh"),
                new Phone("87438807"),
                new Email("alex@example.com"),
                new Address(address),
                new Relation("client"),
                new TransactionStage("prospect"), new java.util.HashSet<>()); // tags
    }

    @Test
    public void test_addressContainsKeyword_returnsTrue() {
        AddressContainsKeywordsPredicate p =
                new AddressContainsKeywordsPredicate(Collections.singletonList("bedok"));
        assertTrue(p.test(personWith("Blk 101 Bedok North Ave 3")));
    }

    @Test
    public void test_addressContainsAnyKeyword_returnsTrue() {
        AddressContainsKeywordsPredicate p =
                new AddressContainsKeywordsPredicate(Arrays.asList("tampines", "bedok"));
        assertTrue(p.test(personWith("120 Bedok South Rd")));
    }

    @Test
    public void test_caseInsensitive_returnsTrue() {
        AddressContainsKeywordsPredicate p =
                new AddressContainsKeywordsPredicate(Collections.singletonList("BeDoK"));
        assertTrue(p.test(personWith("bedok reservoir view")));
    }

    @Test
    public void test_noMatch_returnsFalse() {
        AddressContainsKeywordsPredicate p =
                new AddressContainsKeywordsPredicate(Collections.singletonList("jurong"));
        assertFalse(p.test(personWith("Blk 30 Geylang Street 29, #06-40")));
    }

    @Test
    public void equals_hashcode() {
        AddressContainsKeywordsPredicate a =
                new AddressContainsKeywordsPredicate(Collections.singletonList("bedok"));
        AddressContainsKeywordsPredicate b =
                new AddressContainsKeywordsPredicate(Collections.singletonList("bedok"));
        AddressContainsKeywordsPredicate c =
                new AddressContainsKeywordsPredicate(Collections.singletonList("tampines"));
        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
        assertFalse(a.equals(c));
    }
}

