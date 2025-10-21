package homey.logic.commands;

import static homey.logic.commands.CommandTestUtil.VALID_REMARK_AMY;
import static homey.logic.commands.CommandTestUtil.VALID_REMARK_BOB;
import static homey.logic.commands.CommandTestUtil.assertCommandFailure;
import static homey.logic.commands.RemarkCommand.MESSAGE_ARGUMENTS;
import static homey.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static homey.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static homey.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import homey.logic.parser.RemarkCommandParser;
import homey.model.Model;
import homey.model.ModelManager;
import homey.model.UserPrefs;
import homey.model.person.Remark;




public class RemarkCommandTest {
    private RemarkCommandParser parser = new RemarkCommandParser();

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute() {
        final String remark = "Some remark";

        assertCommandFailure(new RemarkCommand(INDEX_FIRST_PERSON, new Remark(remark)), model,
                String.format(MESSAGE_ARGUMENTS, INDEX_FIRST_PERSON.getOneBased(), remark));
    }

    /*
    @Test
    public void parseCommand_remark() throws Exception {
        assertTrue(parser.parseCommand(RemarkCommand.COMMAND_WORD) instanceof RemarkCommand);
    }
     */

    @Test
    public void equals() {
        final Remark remarkAmy = new Remark(VALID_REMARK_AMY);
        final Remark remarkBob = new Remark(VALID_REMARK_BOB);

        final RemarkCommand standardCommand = new RemarkCommand(INDEX_FIRST_PERSON, remarkAmy);

        // same values -> returns true
        RemarkCommand commandWithSameValues = new RemarkCommand(INDEX_FIRST_PERSON, remarkAmy);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new RemarkCommand(INDEX_SECOND_PERSON, remarkAmy)));

        // different remark -> returns false
        assertFalse(standardCommand.equals(new RemarkCommand(INDEX_FIRST_PERSON, remarkBob)));
    }

}
