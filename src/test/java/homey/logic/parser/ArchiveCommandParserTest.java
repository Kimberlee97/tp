package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.Messages.MESSAGE_INVALID_PERSON_LOWER_BOUND_INDEX;
import static homey.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import homey.logic.commands.ArchiveCommand;
import homey.logic.parser.exceptions.ParseException;

public class ArchiveCommandParserTest {
    private final ArchiveCommandParser parser = new ArchiveCommandParser();

    /**
     * Verifies that a valid argument string (e.g. "1") is correctly parsed into an {@code ArchiveCommand}.
     */
    @Test
    public void parse_validArgs_returnsArchiveCommand() throws Exception {
        ArchiveCommand expected = new ArchiveCommand(INDEX_FIRST_PERSON);
        assertEquals(expected, parser.parse("1"));
    }

    /**
     * Ensures that invalid arguments (e.g. non-numeric input) cause a {@code ParseException} to be thrown.
     */
    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("zero"));
    }


    @Test
    public void parse_emptyArgs_throwsParseException() {
        ParseException ex = assertThrows(ParseException.class, () -> parser.parse("   "));
        assertEquals(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE),
                ex.getMessage());
    }

    @Test
    public void parse_zeroOrNegative_throwsLowerBoundMessage() {
        assertEquals(MESSAGE_INVALID_PERSON_LOWER_BOUND_INDEX,
                assertThrows(ParseException.class, () -> parser.parse("0")).getMessage());
        assertEquals(MESSAGE_INVALID_PERSON_LOWER_BOUND_INDEX,
                assertThrows(ParseException.class, () -> parser.parse("-5")).getMessage());
    }
}
