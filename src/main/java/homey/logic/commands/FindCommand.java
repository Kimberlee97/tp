package homey.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import homey.commons.util.ToStringBuilder;
import homey.logic.Messages;
import homey.model.Model;
import homey.model.person.NameContainsKeywordsPredicate;
import homey.model.person.Person;

/**
 * Finds and lists all persons in address book whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons whose names contain any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters:\n"
            + " - Name: KEYWORD [MORE_KEYWORDS]\n"
            + " - Address: a/KEYWORD [MORE_KEYWORDS]\n"
            + " - Tags: t/KEYWORD [MORE_KEYWORDS]\n"
            + " - Relation: r/KEYWORD\n"
            + "Examples:\n"
            + " find alice bob\n"
            + " find a/bedok north\n"
            + " find t/friend\n"
            + " find r/client";

    // Store generically
    private final Predicate<Person> predicate;

    public FindCommand(NameContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    /** Overload used by address-search (a/...) and tag-search (t/...) and any other future predicates. */
    public FindCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof FindCommand)) {
            return false;
        }
        FindCommand otherFindCommand = (FindCommand) other;
        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
