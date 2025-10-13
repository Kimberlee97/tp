package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static homey.logic.parser.CommandParserTestUtil.assertParseFailure;
import static homey.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import homey.logic.commands.FindCommand;
import homey.model.person.AddressContainsKeywordsPredicate;
import homey.model.person.NameContainsKeywordsPredicate;

/**
 * Represents a parser that interprets user input for the {@code find} command.
 * <p>
 * This class handles two types of searches:
 * <ul>
 *   <li><b>Name-based search</b>: {@code find KEYWORD [MORE_KEYWORDS]} — matches names that contain any keyword.</li>
 *   <li><b>Address-based search</b>: {@code find a/KEYWORD [MORE_KEYWORDS]}
 *   matches addresses that contain any keyword.</li>
 * </ul>
 * <p>
 * The parser performs input validation and throws a {@link homey.logic.parser.exceptions.ParseException}
 * when the user input does not conform to the expected command format.
 * It also supports trimming and sanitizing of user input for robustness.
 */
public class FindCommandParserTest {

    private final FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_addressKeywords_success() {
        String input = " a/bedok north ";
        FindCommand expected = new FindCommand(
                new AddressContainsKeywordsPredicate(Arrays.asList("bedok", "north")));
        assertParseSuccess(parser, input, expected);
    }

    @Test
    public void parse_addressEmpty_throwsParseException() {
        // a/ + spaces -> address-only usage
        assertParseFailure(parser, " a/   ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, addressOnlyUsage()));
    }

    @Test
    public void parse_addressPrefixOnlyWithoutSpaces_throwsParseException() {
        assertParseFailure(parser, "a/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, addressOnlyUsage()));
    }

    @Test
    public void parse_addressPrefixWithSpaces_throwsParseException() {
        assertParseFailure(parser, " a/   ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, addressOnlyUsage()));
    }

    private static String addressOnlyUsage() {
        return "Address: " + FindCommand.COMMAND_WORD + " " + PREFIX_ADDRESS + "KEYWORD [MORE_KEYWORDS]";
    }
}
