package homey.logic.commands;

import static homey.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static homey.logic.commands.CommandTestUtil.assertCommandSuccess;
import static homey.testutil.TypicalPersons.CARL;
import static homey.testutil.TypicalPersons.ELLE;
import static homey.testutil.TypicalPersons.FIONA;
import static homey.testutil.TypicalPersons.getTypicalAddressBook;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import homey.logic.Messages;
import homey.model.AddressBook;
import homey.model.Model;
import homey.model.ModelManager;
import homey.model.UserPrefs;
import homey.model.person.NameContainsKeywordsPredicate;
import homey.model.person.Person;
import homey.model.person.RelationContainsKeywordPredicate;
import homey.model.person.TagContainsKeywordsPredicate;
import homey.model.person.TransactionContainsKeywordPredicate;
import homey.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("first"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("second"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        NameContainsKeywordsPredicate predicate = preparePredicate(" ");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsPredicate predicate = preparePredicate("Kurz Elle Kunz");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(asList(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(asList("keyword"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    @Test
    public void constructorOverload_addressPredicate_constructsSuccessfully() {
        var addressPredicate =
                new homey.model.person.AddressContainsKeywordsPredicate(Collections.singletonList("bedok"));
        FindCommand findCommand = new FindCommand(addressPredicate);

        assertNotNull(findCommand);

        FindCommand findCommandCopy = new FindCommand(
                new homey.model.person.AddressContainsKeywordsPredicate(Collections.singletonList("bedok")));
        assertEquals(findCommand, findCommandCopy);

        assertNotNull(findCommand.toString());
    }

    @Test
    public void constructorOverload_tagPredicate_constructsSuccessfully() {
        TagContainsKeywordsPredicate tagPredicate =
                new TagContainsKeywordsPredicate(Collections.singletonList("friend"));
        FindCommand findCommand = new FindCommand(tagPredicate);

        assertNotNull(findCommand);

        FindCommand findCommandCopy = new FindCommand(
                new TagContainsKeywordsPredicate(Collections.singletonList("friend")));
        assertEquals(findCommand, findCommandCopy);

        assertNotNull(findCommand.toString());
    }

    @Test
    public void execute_tagPredicate_personsFound() {
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(
                asList("friends"));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        CommandResult result = command.execute(model);

        assertNotNull(result);
        assertEquals(expectedModel.getFilteredPersonList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_tagPredicateNoMatch_noPersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(
                asList("nonexistenttag"));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_tagPredicateMultipleKeywords_personsFound() {
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(
                asList("friend", "colleague"));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        CommandResult result = command.execute(model);

        assertNotNull(result);
        assertEquals(expectedModel.getFilteredPersonList(), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod_tagPredicate() {
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(
                asList("friend"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(asList(userInput.split("\\s+")));
    }
    // relation search tests
    @Test
    public void constructorOverload_relationPredicate_constructsSuccessfully() {
        RelationContainsKeywordPredicate relationPredicate =
                new RelationContainsKeywordPredicate("client");
        FindCommand findCommand = new FindCommand(relationPredicate);
        assertNotNull(findCommand);
        FindCommand findCommandCopy = new FindCommand(
                new RelationContainsKeywordPredicate("client"));
        assertEquals(findCommand, findCommandCopy);
        assertNotNull(findCommand.toString());
    }
    @Test
    public void execute_relationPredicateClient_personsFound() {
        RelationContainsKeywordPredicate predicate = new RelationContainsKeywordPredicate("client");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        CommandResult result = command.execute(model);
        assertNotNull(result);
        assertEquals(expectedModel.getFilteredPersonList(), model.getFilteredPersonList());
    }
    @Test
    public void toStringMethod_relationPredicate() {
        RelationContainsKeywordPredicate predicate = new RelationContainsKeywordPredicate("client");
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }
    @Test
    public void equals_relationPredicate() {
        RelationContainsKeywordPredicate clientPredicate =
                new RelationContainsKeywordPredicate("client");
        RelationContainsKeywordPredicate vendorPredicate =
                new RelationContainsKeywordPredicate("vendor");
        FindCommand findClientCommand = new FindCommand(clientPredicate);
        FindCommand findVendorCommand = new FindCommand(vendorPredicate);
        assertTrue(findClientCommand.equals(findClientCommand));
        FindCommand findClientCommandCopy = new FindCommand(
                new RelationContainsKeywordPredicate("client"));
        assertTrue(findClientCommand.equals(findClientCommandCopy));
        assertFalse(findClientCommand.equals(findVendorCommand));
    }
    // transaction stage tests
    @Test
    public void constructorOverload_transactionPredicate_constructsSuccessfully() {
        TransactionContainsKeywordPredicate transactionPredicate =
                new TransactionContainsKeywordPredicate("prospect");
        FindCommand findCommand = new FindCommand(transactionPredicate);

        assertNotNull(findCommand);

        FindCommand findCommandCopy = new FindCommand(
                new TransactionContainsKeywordPredicate("prospect"));
        assertEquals(findCommand, findCommandCopy);

        assertNotNull(findCommand.toString());
    }

    @Test
    public void execute_transactionPredicateProspect_personsFound() {
        TransactionContainsKeywordPredicate predicate = new TransactionContainsKeywordPredicate("prospect");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        CommandResult result = command.execute(model);

        assertNotNull(result);
        assertEquals(expectedModel.getFilteredPersonList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_transactionPredicateNegotiating_personsFound() {
        TransactionContainsKeywordPredicate predicate = new TransactionContainsKeywordPredicate("negotiating");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        CommandResult result = command.execute(model);

        assertNotNull(result);
        assertEquals(expectedModel.getFilteredPersonList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_transactionPredicateClosed_personsFound() {
        TransactionContainsKeywordPredicate predicate = new TransactionContainsKeywordPredicate("closed");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        CommandResult result = command.execute(model);

        assertNotNull(result);
        assertEquals(expectedModel.getFilteredPersonList(), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod_transactionPredicate() {
        TransactionContainsKeywordPredicate predicate = new TransactionContainsKeywordPredicate("prospect");
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    @Test
    public void equals_transactionPredicate() {
        TransactionContainsKeywordPredicate prospectPredicate =
                new TransactionContainsKeywordPredicate("prospect");
        TransactionContainsKeywordPredicate closedPredicate =
                new TransactionContainsKeywordPredicate("closed");

        FindCommand findProspectCommand = new FindCommand(prospectPredicate);
        FindCommand findClosedCommand = new FindCommand(closedPredicate);

        // same object -> returns true
        assertTrue(findProspectCommand.equals(findProspectCommand));

        // same values -> returns true
        FindCommand findProspectCommandCopy = new FindCommand(
                new TransactionContainsKeywordPredicate("prospect"));
        assertTrue(findProspectCommand.equals(findProspectCommandCopy));

        // different transaction stage -> returns false
        assertFalse(findProspectCommand.equals(findClosedCommand));
    }

    @Test
    public void execute_nameMatch_archivedIsExcluded() {
        // Model with: active Alice, archived Bob
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        Person activeAlice = new PersonBuilder().withName("Alice Tan").build();
        Person archivedBob = new PersonBuilder().withName("Bob Ong").build().archived();

        model.addPerson(activeAlice);
        model.addPerson(archivedBob);

        // find "bob" should NOT show archived Bob
        FindCommand cmd = new FindCommand(new NameContainsKeywordsPredicate(List.of("bob")));
        CommandResult result = cmd.execute(model);

        assertEquals(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 0), result.getFeedbackToUser());
        assertEquals(0, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_nameMatch_bothMatchOnlyActiveShown() {
        // Model with: active "Bobby Tan" (matches "bob"), archived "Bob Ong" (also matches)
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        Person activeBobby = new PersonBuilder().withName("Bobby Tan").build();
        Person archivedBob = new PersonBuilder().withName("Bob Ong").build().archived();

        model.addPerson(activeBobby);
        model.addPerson(archivedBob);

        FindCommand cmd = new FindCommand(new NameContainsKeywordsPredicate(asList("bob")));
        CommandResult result = cmd.execute(model);

        assertEquals(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 1), result.getFeedbackToUser());
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals("Bobby Tan", model.getFilteredPersonList().get(0).getName().fullName);
    }
}
