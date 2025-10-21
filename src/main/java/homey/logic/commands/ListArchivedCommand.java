package homey.logic.commands;

import static java.util.Objects.requireNonNull;

import homey.model.Model;

/**
 * Lists archived persons in the address book to the user.
 */
public class ListArchivedCommand extends Command {

    public static final String COMMAND_WORD = "list";
    public static final String COMMAND_ALIAS = "archive";
    public static final String MESSAGE_SUCCESS = "Listed archived persons";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ARCHIVED_PERSONS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
