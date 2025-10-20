package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.Messages.MESSAGE_SINGLE_KEYWORD_ONLY;
import static homey.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static homey.logic.parser.CliSyntax.PREFIX_RELATION;
import static homey.logic.parser.CliSyntax.PREFIX_TAG;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;

import homey.logic.commands.FindCommand;
import homey.logic.parser.exceptions.ParseException;
import homey.model.person.AddressContainsKeywordsPredicate;
import homey.model.person.NameContainsKeywordsPredicate;
import homey.model.person.RelationContainsKeywordPredicate;
import homey.model.person.TagContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new {@link FindCommand}.
 *
 * <p>Supported forms:
 * <ul>
 *   <li>Name search (default): {@code find KEYWORD [MORE_KEYWORDS]}</li>
 *   <li>Address search: {@code find a/KEYWORD [MORE_KEYWORDS]}</li>
 *   <li>Tag search: {@code find t/KEYWORD [MORE_KEYWORDS]}</li>
 * </ul>
 *
 * <p>When {@code a/} is provided without any keywords (e.g., {@code find a/}, {@code find t/}),
 * this parser returns an error showing the address-specific usage only.
 */
public class FindCommandParser implements Parser<FindCommand> {

    @Override
    public FindCommand parse(String args) throws ParseException {
        requireNonNull(args);
        final String trimmedArgs = args.trim();

        // Blank input
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        // Address search: look for the address prefix (e.g., "a/")
        final String addrPrefix = PREFIX_ADDRESS.toString();
        if (trimmedArgs.startsWith(addrPrefix)) {
            final String afterPrefix = trimmedArgs.substring(addrPrefix.length()).trim();

            // a/ with no keywords -> show address-only usage
            if (afterPrefix.isEmpty()) {
                throw new ParseException(String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT, buildAddressOnlyUsage()));
            }

            final List<String> keywords = Arrays.asList(afterPrefix.split("\\s+"));
            return new FindCommand(new AddressContainsKeywordsPredicate(keywords));
        }

        //Tag search: look for the tag prefix (e.g., "t/")
        final String tagPrefix = PREFIX_TAG.toString();
        if (trimmedArgs.startsWith(tagPrefix)) {
            final String afterPrefix = trimmedArgs.substring(tagPrefix.length()).trim();

            // t/ with no keywords -> show tag-only usage
            if (afterPrefix.isEmpty()) {
                throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, buildTagOnlyUsage()));

            }

            final List<String> keywords = Arrays.asList(afterPrefix.split("\\s+"));
            return new FindCommand(new TagContainsKeywordsPredicate(keywords));
        }

        //Relation search: look for the relation prefix (e.g., "r/")
        final String relationPrefix = PREFIX_RELATION.toString();
        if (trimmedArgs.startsWith(relationPrefix)) {
            final String afterPrefix = trimmedArgs.substring(relationPrefix.length()).trim();

            // r/ with no keywords -> show relation-only usage
            if (afterPrefix.isEmpty()) {
                throw new ParseException(String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT, buildRelationOnlyUsage()));
            }

            final List<String> keywords = Arrays.asList(afterPrefix.split("\\s+"));
            // ensure only one keyword is given
            if (keywords.size() > 1) {
                throw new ParseException(String.format(
                        MESSAGE_SINGLE_KEYWORD_ONLY,
                        "Relation",
                        buildRelationOnlyUsage()
                ));
            }
            return new FindCommand(new RelationContainsKeywordPredicate(keywords.get(0)));
        }

        final List<String> nameKeywords = Arrays.asList(trimmedArgs.split("\\s+"));
        return new FindCommand(new NameContainsKeywordsPredicate(nameKeywords));
    }

    /**
     * Builds the address-only usage string for {@code find a/KEYWORD [MORE_KEYWORDS]}.
     */
    private static String buildAddressOnlyUsage() {
        return "Address: " + FindCommand.COMMAND_WORD + " " + PREFIX_ADDRESS + "KEYWORD [MORE_KEYWORDS]";
    }

    /**
     * Builds the tag-only usage string for {@code find t/KEYWORD [MORE_KEYWORDS]}.
     */
    private static String buildTagOnlyUsage() {
        return "Tags: " + FindCommand.COMMAND_WORD + " " + PREFIX_TAG + "KEYWORD [MORE_KEYWORDS]";
    }

    /**
     * Builds the relation-only usage string for {@code find r/KEYWORD}
     */
    private static String buildRelationOnlyUsage() {
        return "Relation: " + FindCommand.COMMAND_WORD + " " + PREFIX_RELATION + "KEYWORD";
    }
}
