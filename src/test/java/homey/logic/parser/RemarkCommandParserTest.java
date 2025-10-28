package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static homey.logic.parser.CliSyntax.PREFIX_REMARK;
import static homey.logic.parser.CommandParserTestUtil.assertParseFailure;
import static homey.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static homey.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import homey.commons.core.index.Index;
import homey.logic.commands.RemarkCommand;
import homey.model.person.Remark;

public class RemarkCommandParserTest {

    private RemarkCommandParser parser = new RemarkCommandParser();
    private String nonEmptyRemark = "Likes natural lighting.";

    @Test
    public void parse_indexSpecified_success() {
        // have remark
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + " " + PREFIX_REMARK + nonEmptyRemark;
        RemarkCommand expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(nonEmptyRemark));
        assertParseSuccess(parser, userInput, expectedCommand);

        // no remark
        userInput = targetIndex.getOneBased() + " " + PREFIX_REMARK;
        expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(""));
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingCompulsoryField_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);

        // no parameters
        assertParseFailure(parser, RemarkCommand.COMMAND_WORD, expectedMessage);

        // no index
        assertParseFailure(parser, RemarkCommand.COMMAND_WORD + " " + nonEmptyRemark, expectedMessage);
    }

    @Test
    public void parse_duplicateRemarkPrefixes_failure() {
        String expectedMessage = getErrorMessageForDuplicatePrefixes(PREFIX_REMARK);
        assertParseFailure(parser, "1 rm/first rm/second", expectedMessage);
    }

    @Test
    public void parse_tooLongRemark_failure() {
        String longRemark = "a".repeat(101);
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, "Remark cannot exceed 100 characters.");
        assertParseFailure(parser, "1 rm/" + longRemark, expectedMessage);
    }

}
