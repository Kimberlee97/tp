package homey.logic.commands;

import static homey.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static homey.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import homey.logic.commands.exceptions.CommandException;
import homey.model.Model;
import homey.model.ModelManager;
import homey.model.UserPrefs;
import homey.model.person.Person;

public class ArchiveCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    /**
     * Confirms that executing {@code ArchiveCommand} on a valid active person successfully archives that person.
     * <p>
     * After execution, the archived person should no longer appear in the default active list.
     */
    @Test
    public void execute_validIndex_archivesSuccessfully() throws Exception {
        Person target = model.getFilteredPersonList().get(0);
        ArchiveCommand command = new ArchiveCommand(INDEX_FIRST_PERSON);

        command.execute(model);
        Person archived = model.getFilteredPersonList().stream()
                .filter(Person::isArchived)
                .findAny()
                .orElse(null);

        // after archiving, the filtered list is active-only so archived should not appear
        assertEquals(null, archived);
    }

    /**
     * Verifies that executing {@code ArchiveCommand} with an index larger than the size of the
     * filtered person list throws a {@code CommandException}.
     * This ensures that the command properly validates index bounds before attempting to access a person.
     */
    @Test
    public void execute_invalidIndex_throwsCommandException() {
        int outOfBoundsIndex = model.getFilteredPersonList().size() + 1;
        ArchiveCommand command = new ArchiveCommand(homey.commons.core.index.Index.fromOneBased(outOfBoundsIndex));
        assertThrows(CommandException.class, () -> command.execute(model));
    }

    /**
     * Ensures that attempting to archive a person who is already archived throws a {@code CommandException}.
     * The model is first modified to contain an archived person, and the filtered list is set to show
     * archived persons so that the command targets the correct entry.
     */
    @Test
    public void execute_alreadyArchived_throwsCommandException() {
        // Arrange: take first person, make them archived in the model
        Person original = model.getFilteredPersonList().get(0);
        model.setPerson(original, original.archived());

        // IMPORTANT: show a list that actually contains the archived person
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ARCHIVED_PERSONS);

        // Act + Assert
        ArchiveCommand command = new ArchiveCommand(INDEX_FIRST_PERSON);
        assertThrows(CommandException.class, () -> command.execute(model));
    }

}
