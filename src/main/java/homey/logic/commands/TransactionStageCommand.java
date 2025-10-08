package homey.logic.commands;

import homey.commons.core.index.Index;
import homey.logic.commands.exceptions.CommandException;
import homey.model.Model;
import homey.model.tag.TransactionStage;

import static homey.commons.util.CollectionUtil.requireAllNonNull;

/**
 * Changes the transaction stage of an existing contact.
 */
public class TransactionStageCommand extends Command {

    public static final String COMMAND_WORD = "transaction";

    public static final String MESSAGE_ARGUMENTS = "Index: %1$d, Stage: %2$s";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the transaction stage of the person identified "
            + "by the index number used in the last person listing. "
            + "Existing transaction stage will be overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "s/ [TRANSACTION STAGE]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + "s/prospect";

    public static final String MESSAGE_NOT_IMPLEMENTED_YET =
            "Transaction Stage command not implemented yet";

    private final Index index;
    private final TransactionStage stage;

    /**
     * @param index of the person in the filtered person list to edit the stage
     * @param stage of the person to be updated to
     */
    public TransactionStageCommand(Index index, TransactionStage stage) {
        requireAllNonNull(index, stage);

        this.index = index;
        this.stage = stage;
    }
    @Override
    public CommandResult execute(Model model) throws CommandException {
        throw new CommandException(
                String.format(MESSAGE_ARGUMENTS, index.getOneBased(), stage));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TransactionStageCommand)) {
            return false;
        }

        TransactionStageCommand e = (TransactionStageCommand) other;
        return index.equals(e.index)
                && stage.equals(e.stage);
    }
}

