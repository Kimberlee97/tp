package homey.logic.commands;

import static java.util.Objects.requireNonNull;

import homey.commons.core.index.Index;
import homey.logic.Messages;
import homey.logic.commands.exceptions.CommandException;
import homey.model.Model;
import homey.model.person.Person;

/**
 * Shows the contact card of the person identified by the index number used in the displayed person list.
 */
public class ViewCommand extends Command {
    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Displays contact information of person at given "
            + "index\n" + "Format: " + COMMAND_WORD + " INDEX";
    public static final String MESSAGE_SUCCESS = "Displaying %1$s";

    private final Index targetIndex;

    public ViewCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (targetIndex.getZeroBased() >= model.getFilteredPersonList().size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person personToView = model.getFilteredPersonList().get(targetIndex.getZeroBased());
        model.setSelectedPerson(personToView);
        return new CommandResult(String.format("Viewing contact: %s", personToView.getName().fullName));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ViewCommand)) {
            return false;
        }
        ViewCommand o = (ViewCommand) other;
        return targetIndex.equals(o.targetIndex);
    }
}
