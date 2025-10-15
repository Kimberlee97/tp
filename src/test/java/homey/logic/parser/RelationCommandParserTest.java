package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.commands.CommandTestUtil.VALID_RELATION_CLIENT;
import static homey.logic.commands.CommandTestUtil.VALID_RELATION_VENDOR;
import static homey.logic.parser.CommandParserTestUtil.assertParseFailure;
import static homey.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static homey.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import homey.commons.core.index.Index;
import homey.logic.commands.RelationCommand;
import homey.model.tag.Relation;

public class RelationCommandParserTest {

    private RelationCommandParser parser = new RelationCommandParser();

    @Test
    public void parse_indexSpecified_success() {
        // have relation
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + " " + VALID_RELATION_CLIENT;
        RelationCommand expectedCommand = new RelationCommand(INDEX_FIRST_PERSON, new Relation(VALID_RELATION_CLIENT));
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingCompulsoryField_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RelationCommand.MESSAGE_USAGE);

        // no parameters
        assertParseFailure(parser, RelationCommand.COMMAND_WORD, expectedMessage);

        // no index
        assertParseFailure(parser, RelationCommand.COMMAND_WORD + " " + VALID_RELATION_VENDOR, expectedMessage);
    }
}
