package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;

import homey.logic.commands.FindCommand;
import homey.logic.parser.exceptions.ParseException;
import homey.model.person.AddressContainsKeywordsPredicate;
import homey.model.person.NameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new {@link FindCommand}.
 * Supports:
 * <ul>
 *   <li>Name search (default): {@code find KEYWORD [MORE_KEYWORDS]}</li>
 *   <li>Address search: {@code find a/KEYWORD [MORE_KEYWORDS]}</li>
 * </ul>
 */
public class FindCommandParser implements Parser<FindCommand> {

    private static final String ADDR_PREFIX = "a/";

    @Override
    public FindCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();

        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        if (trimmedArgs.startsWith(ADDR_PREFIX)) {
            String afterPrefix = trimmedArgs.substring(ADDR_PREFIX.length()).trim();
            if (afterPrefix.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
            }
            List<String> keywords = Arrays.asList(afterPrefix.split("\\s+"));
            return new FindCommand(new AddressContainsKeywordsPredicate(keywords));
        }

        List<String> nameKeywords = Arrays.asList(trimmedArgs.split("\\s+"));
        return new FindCommand(new NameContainsKeywordsPredicate(nameKeywords));
    }
}
