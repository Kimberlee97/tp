package homey.logic.parser;

import static homey.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static homey.logic.parser.CommandParserTestUtil.assertParseFailure;
import static homey.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Map;

import org.junit.jupiter.api.Test;

import homey.logic.commands.AddCommand;
import homey.model.person.Meeting;
import homey.model.person.Person;
import homey.testutil.PersonBuilder;

class AddCommandParserMeetingTest {

    private final AddCommandParser parser = new AddCommandParser();

    @Test
    void parse_invalidMeeting_throwsParseException() {
        // Correct required fields but invalid m/
        String input = " n/Alex p/87438807 e/alex@ex.com a/Blk 30 tr/prospect m/2025-13-40 99:99";
        assertParseFailure(parser, input, Meeting.MESSAGE_CONSTRAINTS);
    }

    @Test
    void parse_missingRequired_stillFailsForUsage() {
        String input = " n/Alex p/87438807 e/alex@ex.com m/2025-11-03 14:00";
        Person partialPerson = new PersonBuilder()
                .withName("Alex")
                .withPhone("87438807")
                .withEmail("alex@ex.com")
                .withMeeting("2025-11-03 14:00")
                .build();
        assertParseSuccess(parser, input,
                new AddCommand(partialPerson, true, Map.of(PREFIX_ADDRESS, "")));
    }
}
