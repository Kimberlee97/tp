package homey.logic.parser;

import static homey.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import homey.logic.commands.HelpCommand;
import homey.logic.parser.exceptions.ParseException;

public class HelpCommandParserTest {

    private HelpCommandParser parser;

    @BeforeEach
    public void setUp() {
        parser = new HelpCommandParser();
    }

    @Test
    public void parse_empty_success() throws Exception {
        HelpCommand cmd = parser.parse("");
        assertTrue(cmd instanceof HelpCommand);
        assertTrue(cmd.getTopic().isEmpty(), "Empty args should yield no topic");
    }

    @Test
    public void parse_validSingleWordTopics_success() throws Exception {
        HelpCommand cmdAdd = parser.parse("add");
        assertEquals(Optional.of("add"), cmdAdd.getTopic());

        HelpCommand cmdEdit = parser.parse("edit");
        assertEquals(Optional.of("edit"), cmdEdit.getTopic());

        HelpCommand cmdExit = parser.parse("exit");
        assertEquals(Optional.of("exit"), cmdExit.getTopic());
    }

    @Test
    public void parse_validMultiWordAndPrefixes_success() throws Exception {
        HelpCommand cmdListMeeting = parser.parse("list meeting");
        assertEquals(Optional.of("list meeting"), cmdListMeeting.getTopic());

        HelpCommand cmdFindAddr = parser.parse("find a/");
        assertEquals(Optional.of("find a/"), cmdFindAddr.getTopic());

        HelpCommand cmdFindTag = parser.parse("find t/");
        assertEquals(Optional.of("find t/"), cmdFindTag.getTopic());

        // New ones you added:
        HelpCommand cmdFindRel = parser.parse("find r/");
        assertEquals(Optional.of("find r/"), cmdFindRel.getTopic());

        HelpCommand cmdFindStage = parser.parse("find s/");
        assertEquals(Optional.of("find s/"), cmdFindStage.getTopic());
    }

    @Test
    public void parse_normalizesWhitespaceAndCase_success() throws Exception {
        HelpCommand cmd = parser.parse("   LiSt    MEeTing   ");
        // parser lowercases and collapses internal whitespace
        assertEquals(Optional.of("list meeting"), cmd.getTopic());
    }

    @Test
    public void parse_invalidNumeric_throws() {
        assertThrows(ParseException.class, () -> parser.parse("3"));
        assertThrows(ParseException.class, () -> parser.parse("  12345  "));
    }

    @Test
    public void parse_invalidUnknownTopic_throws() {
        assertThrows(ParseException.class, () -> parser.parse("random"));
        assertThrows(ParseException.class, () -> parser.parse("find tag"));
        assertThrows(ParseException.class, () -> parser.parse("a/"));
        assertThrows(ParseException.class, () -> parser.parse("add person"));
    }

    @Test
    public void parse_offline_success() throws Exception {
        HelpCommandParser parser = new HelpCommandParser();

        assertTrue(parser.parse("offline") instanceof HelpCommand);
        assertTrue(parser.parse("OFFLINE") instanceof HelpCommand);
        assertTrue(parser.parse("OffLine") instanceof HelpCommand);
    }
}
