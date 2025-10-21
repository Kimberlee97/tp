package homey.logic.commands;

import static homey.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static homey.logic.parser.CliSyntax.PREFIX_EMAIL;
import static homey.logic.parser.CliSyntax.PREFIX_NAME;
import static homey.logic.parser.CliSyntax.PREFIX_PHONE;
import static homey.logic.parser.CliSyntax.PREFIX_TRANSACTION;

import java.util.Map;

import homey.logic.commands.exceptions.CommandException;
import homey.logic.parser.Prefix;
import homey.logic.parser.exceptions.ParseException;

/**
 * Represents an interactive command that can prompt the user for additional input within a singular command.
 */
public abstract class InteractiveCommand extends Command {
    public static final String MESSAGE_INTERACTIVE = "Enter 'cancel' to stop command.";
    protected boolean isInteractive;
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
        if (missingFields.isEmpty()) {
            return null;
        }

        // Define order of fields
        Prefix[] orderedPrefixes = {
            PREFIX_NAME,
            PREFIX_PHONE,
            PREFIX_EMAIL,
            PREFIX_ADDRESS,
            PREFIX_TRANSACTION
        };

        // Return first missing field in order
        for (Prefix prefix : orderedPrefixes) {
            if (missingFields.containsKey(prefix)) {
                return prefix;
            }
        }

        // If none of the ordered fields are missing, return any other missing field
        return missingFields.keySet().iterator().next();
    }
}
