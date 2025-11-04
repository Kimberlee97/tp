package homey.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import homey.commons.core.index.Index;
import homey.logic.Messages;
import homey.logic.commands.exceptions.CommandException;
import homey.model.Model;
import homey.model.person.Person;

/**
 * Unarchives the person identified by the index number used in the displayed archived list.
 * Unarchived persons become visible in the default list again.
 */
public class UnarchiveCommand extends Command {

    public static final String COMMAND_WORD = "unarchive";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Unarchives the person at the given index "
            + "(as shown in the current archived list).\n"
            + "Format: " + COMMAND_WORD + " INDEX";

    public static final String MESSAGE_SUCCESS = "Unarchived: %1$s";
    public static final String MESSAGE_NOT_ARCHIVED = "This person is not archived.";

    private final Index targetIndex;

    /**
     * Creates an {@code UnarchiveCommand} to unarchive the person at {@code targetIndex}.
     *
     * @param targetIndex 1-based index into the current filtered list.
     */
    public UnarchiveCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
    }

    /**
     * Executes the unarchive operation for the person identified by {@code targetIndex}.
     * The command retrieves the person from the currently displayed list, verifies that the index is valid,
     * and that the person is currently archived. It then replaces the archived person with a new
     * unarchived version in the model, and updates the filtered list to show active persons.
     *
     * @param model the {@code Model} which contains the list of persons and handles data modification
     * @return a {@code CommandResult} indicating success and containing the name of the unarchived person
     * @throws CommandException if the index is invalid or the targeted person is not archived
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        final List<Person> lastShownList = model.getFilteredPersonList();
        final int zeroBased = targetIndex.getZeroBased();
        if (zeroBased < 0 || zeroBased >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        final Person personToUnarchive = lastShownList.get(zeroBased);
        if (!personToUnarchive.isArchived()) {
            throw new CommandException(MESSAGE_NOT_ARCHIVED);
        }

        final Person unarchived = personToUnarchive.unarchived();
        model.setPerson(personToUnarchive, unarchived);

        return new CommandResult(String.format(MESSAGE_SUCCESS, unarchived.getName()));
    }

    /**
     * Returns true if both {@code UnarchiveCommand} objects have the same target index.
     * This defines equality between two {@code UnarchiveCommand} instances based on the
     * index of the person they intend to unarchive.
     *
     * @param other the other object to compare with
     * @return true if both commands target the same index; false otherwise
     */
    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof UnarchiveCommand
                && targetIndex.equals(((UnarchiveCommand) other).targetIndex));
    }
}
