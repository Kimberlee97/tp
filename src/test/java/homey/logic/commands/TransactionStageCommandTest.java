package homey.logic.commands;

import static homey.logic.commands.CommandTestUtil.VALID_TRANSACTION_CLOSED;
import static homey.logic.commands.CommandTestUtil.VALID_TRANSACTION_PROSPECT;
import static homey.logic.commands.CommandTestUtil.assertCommandFailure;
import static homey.logic.commands.CommandTestUtil.assertCommandSuccess;
import static homey.logic.commands.CommandTestUtil.showPersonAtIndex;
import static homey.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static homey.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static homey.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import homey.commons.core.index.Index;
import homey.logic.Messages;
import homey.model.AddressBook;
import homey.model.Model;
import homey.model.ModelManager;
import homey.model.UserPrefs;
import homey.model.person.Person;
import homey.model.tag.TransactionStage;
import homey.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code TransactionStageCommand}.
 */
public class TransactionStageCommandTest {
    private static final String STAGE_STUB = TransactionStage.VALID_STAGES[0];
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_changeStageUnfilteredList_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(firstPerson).withStage(STAGE_STUB).build();

        TransactionStageCommand transactionStageCommand = new TransactionStageCommand(INDEX_FIRST_PERSON,
                new TransactionStage(editedPerson.getStage().value));

        String expectedMessage = String.format(TransactionStageCommand.MESSAGE_ADD_TRANSACTION_STAGE_SUCCESS,
                Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);

        assertCommandSuccess(transactionStageCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()))
                .withStage(STAGE_STUB).build();

        TransactionStageCommand transactionStageCommand = new TransactionStageCommand(INDEX_FIRST_PERSON,
                new TransactionStage(editedPerson.getStage().value));

        String expectedMessage = String.format(TransactionStageCommand.MESSAGE_ADD_TRANSACTION_STAGE_SUCCESS,
                Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);

        assertCommandSuccess(transactionStageCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        TransactionStageCommand transactionStageCommand = new TransactionStageCommand(outOfBoundIndex,
                new TransactionStage(VALID_TRANSACTION_PROSPECT));

        assertCommandFailure(transactionStageCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }
    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        TransactionStageCommand transactionStageCommand = new TransactionStageCommand(outOfBoundIndex,
                new TransactionStage(VALID_TRANSACTION_PROSPECT));
        assertCommandFailure(transactionStageCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final TransactionStageCommand standardCommand = new TransactionStageCommand(INDEX_FIRST_PERSON,
                new TransactionStage(VALID_TRANSACTION_PROSPECT));

        // same values -> returns true
        TransactionStageCommand commandWithSameValues = new TransactionStageCommand(INDEX_FIRST_PERSON,
                new TransactionStage(VALID_TRANSACTION_PROSPECT));
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new TransactionStageCommand(INDEX_SECOND_PERSON,
                new TransactionStage(VALID_TRANSACTION_PROSPECT))));

        // different stage -> returns false
        assertFalse(standardCommand.equals(new TransactionStageCommand(INDEX_FIRST_PERSON,
                new TransactionStage(VALID_TRANSACTION_CLOSED))));
    }
}
