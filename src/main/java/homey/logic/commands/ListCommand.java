package homey.logic.commands;

import static homey.model.Model.PREDICATE_SHOW_ACTIVE_PERSONS;
import static java.util.Objects.requireNonNull;

import homey.model.Model;

/**
 * Lists all persons in the address book to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Listed all persons";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists persons.\n"
            + "Usage: list [archive | meeting]\n"
            + "Examples: list | list archive | list meeting";

    @Override
    public CommandResult execute(Model model) {
        model.clearPersonListSorting();
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
