package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import homey.logic.commands.AddCommand;
import homey.model.person.Meeting;

class AddCommandParserMeetingTest {

    private final AddCommandParser parser = new AddCommandParser();

    @Test
    void parse_invalidMeeting_throwsParseException() {
        // Correct required fields but invalid m/
        String input = " n/Alex p/87438807 e/alex@ex.com a/Blk 30 s/prospect m/2025-13-40 99:99";
        assertParseFailure(parser, input, Meeting.MESSAGE_CONSTRAINTS);
    }

    @Test
    void parse_missingRequired_stillFailsForUsage() {
        String input = " n/Alex p/87438807 e/alex@ex.com a/Blk 30 m/2025-11-03 14:00";
        assertParseFailure(parser, input, String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }
}
