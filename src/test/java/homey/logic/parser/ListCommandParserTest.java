package homey.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import homey.logic.commands.Command;
import homey.logic.commands.ListArchivedCommand;
import homey.logic.commands.ListCommand;
import homey.logic.commands.ListMeetingCommand;
import homey.logic.parser.exceptions.ParseException;

/**
 * Contains unit tests for {@code ListCommandParser}, which interprets the "list" command input.
 * Verifies that valid arguments correctly return {@code ListCommand} or {@code ListArchivedCommand},
 * and invalid arguments produce a {@code ParseException}.
 */
public class ListCommandParserTest {
    private final ListCommandParser parser = new ListCommandParser();

    /**
     * Ensures that when no arguments (or only whitespace) are provided,
     * the parser returns a {@code ListCommand} that lists active persons.
     */
    @Test
    public void parse_noArgs_returnsListCommand() throws Exception {
        assertTrue(parser.parse("  ") instanceof ListCommand);
    }

    /**
     * Ensures that when "archive" is provided as an argument,
     * the parser returns a {@code ListArchivedCommand} that lists archived persons.
     */
    @Test
    public void parse_archive_returnsListArchiveCommand() throws Exception {
        assertTrue(parser.parse("archive") instanceof ListArchivedCommand);
    }

    /**
     * Ensures that when an invalid argument (anything other than blank or "archive")
     * is provided, the parser throws a {@code ParseException}.
     */
    @Test
    public void parse_invalidArg_throwsParseException() {
        // Non-numeric junk should be rejected
        assertThrows(ParseException.class, () -> parser.parse("something"));
        assertThrows(ParseException.class, () -> parser.parse("foo bar"));
        assertThrows(ParseException.class, () -> parser.parse("archivee"));
    }

    @Test
    public void parse_meeting_returnsListMeetingCommand() throws Exception {
        Command cmdLower = parser.parse("meeting");
        Command cmdUpper = parser.parse("MEETING");
        assertTrue(cmdLower instanceof ListMeetingCommand);
        assertTrue(cmdUpper instanceof ListMeetingCommand);
    }
}
