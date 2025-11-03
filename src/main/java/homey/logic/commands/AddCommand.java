package homey.logic.commands;

import static homey.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static homey.logic.parser.CliSyntax.PREFIX_EMAIL;
import static homey.logic.parser.CliSyntax.PREFIX_NAME;
import static homey.logic.parser.CliSyntax.PREFIX_PHONE;
import static homey.logic.parser.CliSyntax.PREFIX_RELATION;
import static homey.logic.parser.CliSyntax.PREFIX_REMARK;
import static homey.logic.parser.CliSyntax.PREFIX_TAG;
import static homey.logic.parser.CliSyntax.PREFIX_TRANSACTION;
import static java.util.Objects.requireNonNull;

import java.util.Map;

import homey.commons.util.ToStringBuilder;
import homey.logic.Messages;
import homey.logic.commands.exceptions.CommandException;
import homey.logic.parser.AddCommandParser;
import homey.logic.parser.Prefix;
import homey.logic.parser.exceptions.ParseException;
import homey.model.Model;
import homey.model.person.Person;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends InteractiveCommand {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to the address book. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + PREFIX_ADDRESS + "ADDRESS "
            + PREFIX_TRANSACTION + "TRANSACTION STAGE "
            + "[" + PREFIX_RELATION + "RELATION] "
            + "[" + PREFIX_REMARK + "REMARK] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@example.com "
            + PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 "
            + PREFIX_TRANSACTION + "prospect "
            + PREFIX_RELATION + "client "
            + PREFIX_TAG + "friends "
            + PREFIX_TAG + "owesMoney";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book";
    public static final String MESSAGE_MISSING_NAME = "Enter name.";
    public static final String MESSAGE_MISSING_PHONE = "Enter phone number.";
    public static final String MESSAGE_MISSING_EMAIL = "Enter email.";
    public static final String MESSAGE_MISSING_ADDRESS = "Enter address.";
    public static final String MESSAGE_MISSING_STAGE = "Enter transaction stage (prospect/negotiating/closed).";

    private Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        super(false, null);
        requireNonNull(person);
        toAdd = person;
    }

    /**
     * Creates an interactive AddCommand with missing fields to be filled in.
     */
    public AddCommand(Person person, boolean isInteractive, Map<Prefix, String> missingFields) {
        super(isInteractive, missingFields);
        requireNonNull(person);
        this.toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        // regular command or all missing fields are filled
        if (!isInteractive || missingFields.isEmpty()) {
            boolean duplicateInArchived = model.getAddressBook().getPersonList().stream()
                    .anyMatch(p -> p.isArchived() && p.isSamePerson(toAdd));
            if (duplicateInArchived) {
                throw new CommandException(this.isInteractive
                        ? Messages.MESSAGE_DUPLICATE_IN_ARCHIVED + "\n" + MESSAGE_INTERACTIVE
                        : Messages.MESSAGE_DUPLICATE_IN_ARCHIVED);
            }
            if (model.hasPerson(toAdd)) {
                throw new CommandException(this.isInteractive
                        ? MESSAGE_DUPLICATE_PERSON + "\n" + MESSAGE_INTERACTIVE
                        : MESSAGE_DUPLICATE_PERSON);
            }

            this.isInteractive = false;

            model.addPerson(toAdd);
            String successMessage;
            if (toAdd.getMeeting().isPresent()) {
                successMessage = String.format(
                        "New person added: %s\nNext meeting: %s",
                        Messages.format(toAdd),
                        toAdd.getMeeting().get().toDisplayString()
                );
            } else {
                successMessage = String.format(MESSAGE_SUCCESS, Messages.format(toAdd));
            }

            return new CommandResult(successMessage);
        }

        String prompt = getPromptForField(getNextMissingField()) + "\n" + MESSAGE_INTERACTIVE;
        return new CommandResult(prompt, false, false);
    }

    @Override
    public void updateField(Prefix prefix, String value) throws ParseException {
        super.updateField(prefix, value);
        try {
            this.toAdd = AddCommandParser.updatePersonField(toAdd, prefix, value);
            missingFields.remove(prefix);
        } catch (ParseException pe) {
            missingFields.put(prefix, ""); // Keep the field as missing if parsing fails
            throw new ParseException(pe.getMessage() + "\n" + InteractiveCommand.MESSAGE_INTERACTIVE, pe);
        }
    }

    @Override
    public String getPromptForField(Prefix prefix) {
        switch (prefix.toString()) {
        case "n/":
            return MESSAGE_MISSING_NAME;
        case "p/":
            return MESSAGE_MISSING_PHONE;
        case "e/":
            return MESSAGE_MISSING_EMAIL;
        case "a/":
            return MESSAGE_MISSING_ADDRESS;
        case "s/":
            return MESSAGE_MISSING_STAGE;
        default:
            // should not reach here
            assert false : "Unknown prefix: " + prefix;;
            throw new AssertionError("Unknown prefix: " + prefix);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand)) {
            return false;
        }

        AddCommand otherAddCommand = (AddCommand) other;
        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
