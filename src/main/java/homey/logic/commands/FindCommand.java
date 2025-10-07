package homey.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import homey.model.Model;
import homey.model.person.Person;

/**
 * Finds and lists persons in address book whose name (default) or address (with a/ prefix)
 * matches any of the argument keywords.
 * Keyword matching is case-insensitive and tokens are ANY-match.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds persons by name (default) "
            + "or by address using the address prefix.\n"
            + "Name: find KEYWORD [MORE_KEYWORDS]\n"
            + "Address: find a/KEYWORD [MORE_KEYWORDS]\n"
            + "Examples: find alice    |   find a/bedok   |   find a/bedok north";

    private final Predicate<Person> predicate;

    public FindCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format("%d persons listed.", model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof FindCommand)
                && predicate.equals(((FindCommand) other).predicate);
    }
}
