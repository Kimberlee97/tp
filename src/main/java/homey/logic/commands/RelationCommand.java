package homey.logic.commands;

import static homey.commons.util.CollectionUtil.requireAllNonNull;

import homey.commons.core.index.Index;
import homey.logic.commands.exceptions.CommandException;
import homey.model.Model;

/**
 * Changes the relation tag of an existing person in the address book.
 */
public class RelationCommand extends Command {

    public static final String COMMAND_WORD = "relation";

    public static final String MESSAGE_ARGUMENTS = "Index: %1$d, Relation: %2$s";

    private final Index index;
    private final String relation;

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits relation tag of the person identified "
            + "by the index number used in the last person listing. "
            + "Existing relation tag will be overwritten.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[client/vendor]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + "client";

    public static final String MESSAGE_NOT_IMPLEMENTED_YET =
            "Relation command not implemented yet";

    /**
     * @param index of the person in the filtered person list to edit the remark
     * @param relation of the person to be updated to
     */
    public RelationCommand(Index index, String relation) {
        requireAllNonNull(index, relation);

        this.index = index;
        this.relation = relation;
    }
    @Override
    public CommandResult execute(Model model) throws CommandException {
        throw new CommandException(
                String.format(MESSAGE_ARGUMENTS, index.getOneBased(), relation));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RelationCommand)) {
            return false;
        }

        RelationCommand e = (RelationCommand) other;
        return index.equals(e.index)
                && relation.equals(e.relation);
    }
}
