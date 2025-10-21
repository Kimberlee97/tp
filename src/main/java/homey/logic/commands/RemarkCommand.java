package homey.logic.commands;

import homey.model.Model;

/**
 * Changes the remark of an existing person.
 */
public class RemarkCommand extends Command {
    public static final String COMMAND_WORD = "remark";

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult("Hello from remark");
    }
}
