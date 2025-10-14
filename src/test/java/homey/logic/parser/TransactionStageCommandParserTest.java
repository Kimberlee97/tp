package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.parser.CliSyntax.PREFIX_TRANSACTION;
import static homey.logic.parser.CommandParserTestUtil.assertParseFailure;
import static homey.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static homey.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import homey.commons.core.index.Index;
import homey.logic.commands.TransactionStageCommand;
import homey.model.tag.TransactionStage;

public class TransactionStageCommandParserTest {

    private TransactionStageCommandParser parser = new TransactionStageCommandParser();

    private String nonEmptyTransaction = "prospect";

    @Test
    public void parse_indexSpecified_success() {
        // have valid transaction stage
        Index targetIndex = INDEX_FIRST_PERSON;

        for (int i = 0; i < TransactionStage.VALID_STAGES.length; i++) {
            String currentStage = TransactionStage.VALID_STAGES[i];
            String userInput = targetIndex.getOneBased() + " " + PREFIX_TRANSACTION + currentStage;
            TransactionStageCommand expectedCommand = new TransactionStageCommand(INDEX_FIRST_PERSON,
                    new TransactionStage(currentStage));
            assertParseSuccess(parser, userInput, expectedCommand);
        }
    }

    @Test
    public void parse_missingCompulsoryField_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, TransactionStageCommand.MESSAGE_USAGE);

        // no parameters
        assertParseFailure(parser, TransactionStageCommand.COMMAND_WORD, expectedMessage);

        // no index
        assertParseFailure(parser, TransactionStageCommand.COMMAND_WORD + " " + nonEmptyTransaction,
                expectedMessage);

        // no transaction stage
        assertParseFailure(parser, TransactionStageCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON + " ",
                expectedMessage);
    }

    @Test
    public void parse_invalidCompulsoryField_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, TransactionStageCommand.MESSAGE_USAGE);
        String invalidTransaction = "potential";

        // invalid transaction stage
        assertParseFailure(parser, TransactionStageCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON + " "
                + invalidTransaction, expectedMessage);
    }

}
