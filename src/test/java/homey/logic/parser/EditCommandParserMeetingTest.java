package homey.logic.parser;

import static homey.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import homey.logic.parser.exceptions.ParseException;
import homey.model.person.Meeting;

class EditCommandParserMeetingTest {

    private final EditCommandParser parser = new EditCommandParser();

    @Test
    void parse_invalidMeeting_throwsParseException() {
        // index present but invalid meeting content
        String input = " 1 m/2025-13-40 99:99";
        assertParseFailure(parser, input, Meeting.MESSAGE_CONSTRAINTS);
    }

    @Test
    void parse_missingIndex_throwsParseException() {
        // No index supplied
        String input = " m/2025-11-03 14:00";
        try {
            parser.parse(input);
        } catch (ParseException e) {
            // pass if any ParseException is thrown
            return;
        }
        throw new AssertionError("Expected ParseException not thrown.");
    }
}
