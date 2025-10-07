package homey.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import homey.logic.commands.FindCommand;
import homey.logic.parser.exceptions.ParseException;
import homey.model.person.AddressContainsKeywordsPredicate;
import homey.model.person.NameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object.
 * Supports:
 *   - Name search (default): find KEYWORD [MORE_KEYWORDS]
 *   - Address search:        find a/KEYWORD [MORE_KEYWORDS]
 */
public class FindCommandParser implements Parser<FindCommand> {

    private static final String ADDR_PREFIX = "a/";

    @Override
    public FindCommand parse(String args) throws ParseException {
        requireNonNull(args);
        final String trimmed = args.trim();

        if (trimmed.isEmpty()) {
            throw new ParseException(FindCommand.MESSAGE_USAGE);
        }

        // Address mode: find a/bedok [north ...]
        if (trimmed.startsWith(ADDR_PREFIX)) {
            String afterPrefix = trimmed.substring(ADDR_PREFIX.length()).trim();
            if (afterPrefix.isEmpty()) {
                throw new ParseException(FindCommand.MESSAGE_USAGE);
            }

            List<String> tokens = splitOnWhitespace(afterPrefix);
            return new FindCommand(new AddressContainsKeywordsPredicate(tokens));
        }

        // Default: Name mode (existing behaviour)
        List<String> nameKeywords = splitOnWhitespace(trimmed);
        return new FindCommand(new NameContainsKeywordsPredicate(nameKeywords));
    }

    private static List<String> splitOnWhitespace(String s) {
        String[] parts = s.split("\\s+");
        if (parts.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(parts);
    }
}
