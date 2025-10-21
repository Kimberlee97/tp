package homey.logic.parser;

import static homey.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import homey.logic.commands.UnarchiveCommand;
import homey.logic.parser.exceptions.ParseException;

public class UnarchiveCommandParserTest {
    private final UnarchiveCommandParser parser = new UnarchiveCommandParser();

    /**
     * Verifies that a valid argument string (e.g. "1") is correctly parsed into an {@code UnarchiveCommand}
     * targeting the corresponding person index.
     * This ensures that the parser properly converts numeric input into a valid command object.
     */
    @Test
    public void parse_validArgs_returnsUnarchiveCommand() throws Exception {
        UnarchiveCommand expected = new UnarchiveCommand(INDEX_FIRST_PERSON);
        assertEquals(expected, parser.parse("1"));
    }

    /**
     * Ensures that invalid arguments (e.g. non-numeric input, empty string, or extra text)
     * cause a {@code ParseException} to be thrown when parsing {@code UnarchiveCommand}.
     * <p>
     * This confirms that the parser properly rejects malformed or out-of-format input.
     */
    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("zero"));
        assertThrows(ParseException.class, () -> parser.parse("")); // empty input
        assertThrows(ParseException.class, () -> parser.parse("1 extra")); // extra text
    }
}
