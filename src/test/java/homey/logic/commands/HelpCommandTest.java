package homey.logic.commands;

import static homey.logic.commands.CommandTestUtil.assertCommandSuccess;
import static homey.logic.commands.HelpCommand.SHOWING_HELP_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import homey.model.Model;
import homey.model.ModelManager;

public class HelpCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    @Test
    public void execute_help_success() {
        CommandResult expectedCommandResult = new CommandResult(SHOWING_HELP_MESSAGE, true, false);
        assertCommandSuccess(new HelpCommand(), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_helpWithTopic_success() {
        CommandResult expected = new CommandResult(SHOWING_HELP_MESSAGE, true,
                false, Optional.of("add"));
        assertCommandSuccess(new HelpCommand("add"), model, expected, expectedModel);
    }

    @Test
    public void equals_sameTopic_true() {
        assertEquals(new HelpCommand("add"), new HelpCommand("add"));
    }

    @Test
    public void equals_differentTopic_false() {
        assertNotEquals(new HelpCommand("add"), new HelpCommand("edit"));
    }

    @Test
    public void equals_noTopicVsTopic_false() {
        assertNotEquals(new HelpCommand(), new HelpCommand("add"));
    }
}
