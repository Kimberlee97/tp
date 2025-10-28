package homey.logic.commands;

import static homey.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;

import homey.commons.core.index.Index;
import homey.logic.Messages;
import homey.logic.commands.exceptions.CommandException;
import homey.model.Model;
import homey.model.person.Person;
import homey.model.tag.TransactionStage;

/**
 * Changes the transaction stage of an existing contact.
 */
public class TransactionStageCommand extends Command {

    public static final String COMMAND_WORD = "transaction";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the transaction stage of the person identified "
            + "by the index number used in the last person listing. "
            + "Existing transaction stage will be overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "s/ [TRANSACTION STAGE]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + "s/prospect";

    public static final String MESSAGE_ADD_TRANSACTION_STAGE_SUCCESS = "Added transaction stage to Person: %1$s";

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
        requireAllNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = new Person(
                personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getAddress(), personToEdit.getRelation(), stage, personToEdit.getRemark(),
                personToEdit.getTags(), personToEdit.getMeeting());

        if (personToEdit.isArchived()) {
            editedPerson = editedPerson.archived();
        }
        model.setPerson(personToEdit, editedPerson);

        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    /**
     * Generates a command execution success message based on whether
     * the transaction stage is added to or removed from
     * {@code personToEdit}.
     */
    private String generateSuccessMessage(Person personToEdit) {
        return String.format(MESSAGE_ADD_TRANSACTION_STAGE_SUCCESS, Messages.format(personToEdit));
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

