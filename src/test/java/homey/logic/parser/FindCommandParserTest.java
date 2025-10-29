package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.Messages.MESSAGE_SINGLE_KEYWORD_ONLY;
import static homey.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static homey.logic.parser.CliSyntax.PREFIX_RELATION;
import static homey.logic.parser.CliSyntax.PREFIX_TAG;
import static homey.logic.parser.CliSyntax.PREFIX_TRANSACTION;
import static homey.logic.parser.CommandParserTestUtil.assertParseFailure;
import static homey.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import homey.logic.Messages;
import homey.logic.commands.FindCommand;
import homey.model.person.AddressContainsKeywordsPredicate;
import homey.model.person.NameContainsKeywordsPredicate;
import homey.model.person.RelationContainsKeywordPredicate;
import homey.model.person.TagContainsKeywordsPredicate;
import homey.model.person.TransactionContainsKeywordPredicate;



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

    @Test
    public void parse_tagKeywords_success() {
        String input = " t/friend colleague";
        FindCommand expected = new FindCommand(
                new TagContainsKeywordsPredicate(Arrays.asList("friend", "colleague")));
        assertParseSuccess(parser, input, expected);
    }

    @Test
    public void parse_tagKeywordsSingleWord_success() {
        String input = " t/friend";
        FindCommand expected = new FindCommand(
                new TagContainsKeywordsPredicate(Arrays.asList("friend")));
        assertParseSuccess(parser, input, expected);
    }

    @Test
    public void parse_tagKeywordsWithExtraWhitespace_success() {
        String input = " t/  friend   colleague  family  ";
        FindCommand expected = new FindCommand(
                new TagContainsKeywordsPredicate(Arrays.asList("friend", "colleague", "family")));
        assertParseSuccess(parser, input, expected);
    }

    @Test
    public void parse_tagEmpty_throwsParseException() {
        // t/ + spaces -> tag-only usage
        assertParseFailure(parser, " t/   ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, tagOnlyUsage()));
    }

    @Test
    public void parse_tagPrefixOnlyWithoutSpaces_throwsParseException() {
        assertParseFailure(parser, "t/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, tagOnlyUsage()));
    }

    @Test
    public void parse_tagPrefixWithSpaces_throwsParseException() {
        assertParseFailure(parser, " t/   ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, tagOnlyUsage()));
    }

    @Test
    public void parse_tagKeywordWithNonAlphanumericCharacters_throwsParseException() {
        assertParseFailure(parser, "t/friend_buyer",
                String.format("Invalid keyword. Tags can only contain alphanumeric characters"));
    }

    @Test
    public void parse_tagKeywordsWithNewlines_success() {
        String input = " t/\n friend \n \t colleague  \t";
        FindCommand expected = new FindCommand(
                new TagContainsKeywordsPredicate(Arrays.asList("friend", "colleague")));
        assertParseSuccess(parser, input, expected);
    }

    private static String tagOnlyUsage() {
        return "Tags: " + FindCommand.COMMAND_WORD + " " + PREFIX_TAG + "KEYWORD [MORE_KEYWORDS]";
    }

    // relation search tests
    @Test
    public void parse_relationKeywordClient_success() {
        String input = "r/client";
        FindCommand expected = new FindCommand(
                new RelationContainsKeywordPredicate("client"));
        assertParseSuccess(parser, input, expected);
    }

    @Test
    public void parse_relationKeywordVendor_success() {
        String input = " r/vendor";
        FindCommand expected = new FindCommand(
                new RelationContainsKeywordPredicate("vendor"));
        assertParseSuccess(parser, input, expected);
    }

    @Test
    public void parse_relationKeywordClientUpperCase_success() {
        String input = " r/CLIENT";
        FindCommand expected = new FindCommand(
                new RelationContainsKeywordPredicate("client"));
        assertParseSuccess(parser, input, expected);
    }

    @Test
    public void parse_relationEmpty_throwsParseException() {
        assertParseFailure(parser, " r/   ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, relationOnlyUsage()));
    }

    @Test
    public void parse_relationPrefixOnlyWithoutSpaces_throwsParseException() {
        assertParseFailure(parser, "r/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, relationOnlyUsage()));
    }

    @Test
    public void parse_relationInvalidKeyword_throwsParseException() {
        assertParseFailure(parser, " r/supplier",
                "Invalid relation. Only 'client' or 'vendor' are allowed");
    }

    @Test
    public void parse_relationMultipleKeywords_throwsParseException() {
        assertParseFailure(parser, " r/client vendor",
                String.format(MESSAGE_SINGLE_KEYWORD_ONLY, "Relation", relationOnlyUsage()));
    }

    private static String relationOnlyUsage() {
        return "Relation: " + FindCommand.COMMAND_WORD + " " + PREFIX_RELATION + "KEYWORD";
    }
    // transaction stage tests
    @Test
    public void parse_transactionKeywordProspect_success() {
        String input = " s/prospect";
        FindCommand expected = new FindCommand(
                new TransactionContainsKeywordPredicate("prospect"));
        assertParseSuccess(parser, input, expected);
    }

    @Test
    public void parse_transactionKeywordNegotiating_success() {
        String input = " s/negotiating";
        FindCommand expected = new FindCommand(
                new TransactionContainsKeywordPredicate("negotiating"));
        assertParseSuccess(parser, input, expected);
    }

    @Test
    public void parse_transactionKeywordClosed_success() {
        String input = " s/closed";
        FindCommand expected = new FindCommand(
                new TransactionContainsKeywordPredicate("closed"));
        assertParseSuccess(parser, input, expected);
    }

    @Test
    public void parse_transactionKeywordUpperCase_success() {
        String input = " s/PROSPECT";
        FindCommand expected = new FindCommand(
                new TransactionContainsKeywordPredicate("prospect"));
        assertParseSuccess(parser, input, expected);
    }

    @Test
    public void parse_transactionEmpty_throwsParseException() {
        assertParseFailure(parser, " s/   ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, transactionOnlyUsage()));
    }

    @Test
    public void parse_transactionPrefixOnlyWithoutSpaces_throwsParseException() {
        assertParseFailure(parser, "s/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, transactionOnlyUsage()));
    }

    @Test
    public void parse_transactionInvalidKeyword_throwsParseException() {
        assertParseFailure(parser, " s/pending",
                "Invalid transaction stage. Only 'prospect' or 'negotiating' or 'closed' are allowed");
    }

    @Test
    public void parse_transactionMultipleKeywords_throwsParseException() {
        assertParseFailure(parser, " s/prospect closed",
                String.format(MESSAGE_SINGLE_KEYWORD_ONLY, "Transaction stage", transactionOnlyUsage()));
    }

    private static String transactionOnlyUsage() {
        return "Transaction stage: " + FindCommand.COMMAND_WORD + " " + PREFIX_TRANSACTION + "KEYWORD";
    }

    // invalid prefix tests
    @Test
    public void parse_invalidPrefix_throwsParseException() {
        assertParseFailure(parser, " x/keyword",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidPrefixMultipleCharacters_throwsParseException() {
        assertParseFailure(parser, " ab/keyword",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateAddressPrefix_throwsParseException() {
        assertParseFailure(parser, " a/bedok a/north",
                Messages.MESSAGE_DUPLICATE_FIELDS + "a/");
    }

    @Test
    public void parse_duplicateTagPrefix_throwsParseException() {
        assertParseFailure(parser, " t/friend t/colleague",
                Messages.MESSAGE_DUPLICATE_FIELDS + "t/");
    }

    @Test
    public void parse_duplicateRelationPrefix_throwsParseException() {
        assertParseFailure(parser, " r/client r/vendor",
                Messages.MESSAGE_DUPLICATE_FIELDS + "r/");
    }

    @Test
    public void parse_duplicateTransactionPrefix_throwsParseException() {
        assertParseFailure(parser, " s/prospect s/closed s/negotiating",
                Messages.MESSAGE_DUPLICATE_FIELDS + "s/");
    }

    @Test
    public void parse_duplicateEmptyPrefixes_throwsParseException() {
        assertParseFailure(parser, " t/ t/",
                Messages.MESSAGE_DUPLICATE_FIELDS + "t/");
    }

    // multiple different prefix
    @Test
    public void parse_mixedAddressAndTag_throwsParseException() {
        assertParseFailure(parser, " a/Bedok t/friend",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_mixedTagAndRelation_throwsParseException() {
        assertParseFailure(parser, " t/client r/client",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_mixedRelationAndTransaction_throwsParseException() {
        assertParseFailure(parser, " r/client s/prospect",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_mixedEmptyTagAndAddress_throwsParseException() {
        assertParseFailure(parser, " t/ a/Bedok",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_mixedThreePrefixes_throwsParseException() {
        assertParseFailure(parser, " a/bedok t/friend r/client",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }
}
