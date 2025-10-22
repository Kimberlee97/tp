package homey.logic.commands;

import static homey.commons.util.CollectionUtil.requireAllNonNull;
import static homey.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import homey.commons.core.index.Index;
import homey.logic.Messages;
import homey.logic.commands.exceptions.CommandException;
import homey.model.Model;
import homey.model.person.Person;
import homey.model.tag.Relation;

/**
 * Changes the relation tag of an existing person in the address book.
 */
public class RelationCommand extends Command {

    public static final String COMMAND_WORD = "relation";

    public static final String MESSAGE_ADD_RELATION_SUCCESS = "Added relation %1$s to Person: %2$s";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits relation tag of the person identified "
            + "by the index number used in the last person listing. "
            + "Existing relation tag will be overwritten.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[client/vendor]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + "client";

    private final Index index;
    private final Relation relation;

    /**
     * @param index of the person in the filtered person list to edit the relation
     * @param relation of the person to be updated to
     */
    public RelationCommand(Index index, Relation relation) {
        requireAllNonNull(index, relation);

        this.index = index;
        this.relation = relation;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = new Person(
                personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getAddress(), relation, personToEdit.getStage(), personToEdit.getRemark(),
                personToEdit.getTags());

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    /**
     * Generates a command execution success message based on whether
     * the relation added to {@code personToEdit}.
     */
    private String generateSuccessMessage(Person personToEdit) {
        return String.format(MESSAGE_ADD_RELATION_SUCCESS, relation.value, Messages.format(personToEdit));
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
