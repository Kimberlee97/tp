package homey.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import homey.commons.core.index.Index;
import homey.logic.Messages;
import homey.logic.commands.exceptions.CommandException;
import homey.model.Model;
import homey.model.person.Person;

/**
 * Archives the person identified by the index number used in the displayed person list.
 * Archived persons are hidden from the default list.
 */
public class ArchiveCommand extends Command {

    public static final String COMMAND_WORD = "archive";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Archives the person at the given index "
            + "(as shown in the current list).\n"
            + "Format: " + COMMAND_WORD + " INDEX";

    public static final String MESSAGE_SUCCESS = "Archived: %1$s";
    public static final String MESSAGE_ALREADY_ARCHIVED = "This person is already archived.";

    private final Index targetIndex;

    /**
     * Creates an {@code ArchiveCommand} to archive the person at {@code targetIndex}.
     *
     * @param targetIndex 1-based index into the current filtered list.
     */
    public ArchiveCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        final List<Person> lastShownList = model.getFilteredPersonList();
        final int zeroBased = targetIndex.getZeroBased();
        if (zeroBased < 0 || zeroBased >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        final Person personToArchive = lastShownList.get(zeroBased);
        if (personToArchive.isArchived()) {
            throw new CommandException(MESSAGE_ALREADY_ARCHIVED);
        }

        final Person archived = personToArchive.archived();
        model.setPerson(personToArchive, archived);
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ACTIVE_PERSONS); // keep list showing active persons only

        return new CommandResult(String.format(MESSAGE_SUCCESS, archived.getName()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof ArchiveCommand
                && targetIndex.equals(((ArchiveCommand) other).targetIndex));
    }
}
