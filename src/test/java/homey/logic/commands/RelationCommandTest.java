package homey.logic.commands;

import static homey.logic.commands.CommandTestUtil.VALID_RELATION_CLIENT;
import static homey.logic.commands.CommandTestUtil.VALID_RELATION_VENDOR;
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
import homey.model.tag.Relation;
import homey.testutil.PersonBuilder;

public class RelationCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_addRelationUnfilteredList_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(firstPerson).withRelation(VALID_RELATION_CLIENT).build();

        RelationCommand relationCommand = new RelationCommand(
                INDEX_FIRST_PERSON, new Relation(editedPerson.getRelation().value));

        String expectedMessage = String.format(
                RelationCommand.MESSAGE_ADD_RELATION_SUCCESS, VALID_RELATION_CLIENT, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);

        assertCommandSuccess(relationCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()))
                .withRelation(VALID_RELATION_CLIENT).build();

        RelationCommand relationCommand = new RelationCommand(
                INDEX_FIRST_PERSON, new Relation(editedPerson.getRelation().value));

        String expectedMessage = String.format(
                RelationCommand.MESSAGE_ADD_RELATION_SUCCESS, VALID_RELATION_CLIENT, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);
        expectedModel.setPerson(expectedModel.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(relationCommand, model, expectedMessage, expectedModel);
    }


    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        RelationCommand relationCommand = new RelationCommand(outOfBoundIndex, new Relation(VALID_RELATION_VENDOR));

        assertCommandFailure(relationCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
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

        RelationCommand relationCommand = new RelationCommand(outOfBoundIndex, new Relation(VALID_RELATION_VENDOR));

        assertCommandFailure(relationCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final RelationCommand standardCommand = new RelationCommand(INDEX_FIRST_PERSON,
                new Relation(VALID_RELATION_CLIENT));

        // same values -> returns true
        RelationCommand commandWithSameValues = new RelationCommand(INDEX_FIRST_PERSON,
                new Relation(VALID_RELATION_CLIENT));
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new RelationCommand(INDEX_SECOND_PERSON,
                new Relation(VALID_RELATION_CLIENT))));

        // different relation -> returns false
        assertFalse(standardCommand.equals(new RelationCommand(INDEX_FIRST_PERSON,
                new Relation(VALID_RELATION_VENDOR))));
    }
}
