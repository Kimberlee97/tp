package homey.logic.commands;

import java.util.Map;

import homey.logic.commands.exceptions.CommandException;
import homey.logic.parser.Prefix;
import homey.logic.parser.exceptions.ParseException;

/**
 * Represents an interactive command that can prompt the user for additional input within a singular command.
 */
public abstract class InteractiveCommand extends Command {
    protected final boolean isInteractive;
    protected Map<Prefix, String> missingFields;

    /**
     * Creates an interactive command with the specified missing fields.
     */
    public InteractiveCommand(boolean isInteractive, Map<Prefix, String> missingFields) {
        this.isInteractive = isInteractive;
        this.missingFields = missingFields;
    }

    public boolean isInteractive() {
        return isInteractive;
    }

    public Map<Prefix, String> getMissingFields() {
        return missingFields;
    }

    public void updateField(Prefix field, String value) throws ParseException {
        missingFields.put(field, value);
    }

    public abstract String getPromptForField(Prefix prefix) throws CommandException;

    public Prefix getNextMissingField() {
        return missingFields.isEmpty() ? null : missingFields.keySet().iterator().next();
    }
}
