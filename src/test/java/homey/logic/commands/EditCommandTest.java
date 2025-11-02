package homey.logic.commands;

import static homey.logic.commands.CommandTestUtil.DESC_AMY;
import static homey.logic.commands.CommandTestUtil.DESC_BOB;
import static homey.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static homey.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static homey.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static homey.logic.commands.CommandTestUtil.assertCommandFailure;
import static homey.logic.commands.CommandTestUtil.assertCommandSuccess;
import static homey.logic.commands.CommandTestUtil.showPersonAtIndex;
import static homey.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static homey.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static homey.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import homey.commons.core.index.Index;
import homey.logic.Messages;
import homey.logic.commands.EditCommand.EditPersonDescriptor;
import homey.logic.commands.exceptions.CommandException;
import homey.model.AddressBook;
import homey.model.Model;
import homey.model.ModelManager;
import homey.model.UserPrefs;
import homey.model.person.Meeting;
import homey.model.person.Person;
import homey.testutil.EditPersonDescriptorBuilder;
import homey.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastPerson, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptor());
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(
                INDEX_FIRST_PERSON, new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        expectedModel.setPerson(expectedModel.getFilteredPersonList().get(0), editedPerson);
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in address book
        Person personInList = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
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

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_PERSON, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(index, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

    @Test
    public void execute_clearMeeting_showsClearMessage() throws Exception {
        Model localModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        Person original = localModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person withMeeting = new PersonBuilder(original).withMeeting("2025-11-10 09:30").build();
        localModel.setPerson(original, withMeeting);

        // Descriptor that clears meeting (m/)
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setMeeting(Optional.empty());

        EditCommand cmd = new EditCommand(INDEX_FIRST_PERSON, descriptor);
        CommandResult result = cmd.execute(localModel);

        // Message check
        String expectedPrefix = "Cleared meeting for " + withMeeting.getName();
        assertTrue(result.getFeedbackToUser().startsWith(expectedPrefix));

        // State check: meeting is cleared
        Person after = localModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(after.getMeeting().isEmpty());
    }

    @Test
    public void execute_setMeeting_showsUpdatedMessage() throws Exception {
        Model localModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        // Ensure the first person starts WITHOUT a meeting (default builder has none)
        Person original = localModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person withoutMeeting = new PersonBuilder(original).build();
        localModel.setPerson(original, withoutMeeting);

        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setMeeting(Optional.of(new Meeting("2025-11-11 09:30")));

        EditCommand cmd = new EditCommand(INDEX_FIRST_PERSON, descriptor);
        CommandResult result = cmd.execute(localModel);

        // Message check
        assertTrue(result.getFeedbackToUser().startsWith("Updated meeting for "));
        assertTrue(result.getFeedbackToUser().contains("2025-11-11 09:30"));

        // State check: meeting is set
        Person after = localModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(after.getMeeting().isPresent());
        assertEquals("2025-11-11 09:30", after.getMeeting().get().toDisplayString());
    }

    @Test
    public void execute_clearMeetingWhenNone_showsNoMeetingsToClearMessage() throws Exception {
        Model localModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Person original = localModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person withoutMeeting = new PersonBuilder(original).build();
        localModel.setPerson(original, withoutMeeting);

        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setMeeting(Optional.empty());

        EditCommand cmd = new EditCommand(INDEX_FIRST_PERSON, descriptor);
        CommandResult result = cmd.execute(localModel);

        assertTrue(result.getFeedbackToUser().contains("No meetings to clear for"));
    }

    @Test
    public void execute_archivedTarget_throwsCommandException() {
        Model model = new ModelManager();
        Person archivedBob = new PersonBuilder().withName("Bob Ong").build().archived();
        model.addPerson(archivedBob);
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ARCHIVED_PERSONS);
        EditPersonDescriptor desc = new EditPersonDescriptorBuilder()
                .withPhone("99999999")
                .build();

        EditCommand cmd = new EditCommand(Index.fromOneBased(1), desc);

        CommandException ex = assertThrows(CommandException.class, () -> cmd.execute(model));
        assertEquals(Messages.MESSAGE_CANNOT_EDIT_ARCHIVED, ex.getMessage());
    }

}
