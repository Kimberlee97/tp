package homey.logic.commands;

import static homey.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static homey.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import homey.commons.core.index.Index;
import homey.logic.commands.exceptions.CommandException;
import homey.model.Model;
import homey.model.ModelManager;
import homey.model.UserPrefs;
import homey.model.person.Person;

public class UnarchiveCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    /**
     * Ensures that executing {@code UnarchiveCommand} on a valid archived person
     * successfully unarchives the person and restores them to the active list.
     * The test first archives the first person in the list, switches the filter to show archived persons,
     * and verifies that after execution, the person is no longer archived.
     */
    @Test
    public void execute_validIndex_unarchivesSuccessfully() throws Exception {
        // Arrange: archive the first active person
        Person original = model.getFilteredPersonList().get(0);
        model.setPerson(original, original.archived());

        // Show archived list so INDEX_FIRST_PERSON points to an archived person
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ARCHIVED_PERSONS);

        // Act
        UnarchiveCommand command = new UnarchiveCommand(INDEX_FIRST_PERSON);
        command.execute(model);

        // After execute(), UnarchiveCommand switches filter back to active
        // Assert: the person is no longer archived
        Person nowActive = model.getFilteredPersonList().stream()
                .filter(p -> p.getName().equals(original.getName()))
                .findFirst()
                .orElseThrow();
        assert !nowActive.isArchived();
    }

    /**
     * Verifies that executing {@code UnarchiveCommand} with an index outside the bounds
     * of the current filtered person list throws a {@code CommandException}.
     * This ensures that invalid indices are correctly detected and rejected.
     */
    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        UnarchiveCommand command = new UnarchiveCommand(outOfBoundIndex);
        assertThrows(CommandException.class, () -> command.execute(model));
    }

    /**
     * Ensures that executing {@code UnarchiveCommand} on a person who is not archived
     * throws a {@code CommandException}.
     * This confirms that only archived persons can be unarchived.
     */
    @Test
    public void execute_notArchived_throwsCommandException() {
        UnarchiveCommand command = new UnarchiveCommand(INDEX_FIRST_PERSON);
        assertThrows(CommandException.class, () -> command.execute(model));
    }
}
