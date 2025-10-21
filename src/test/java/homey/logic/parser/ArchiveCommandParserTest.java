package homey.logic.parser;

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
}
