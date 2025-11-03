package homey.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.logging.Logger;

import homey.commons.core.GuiSettings;
import homey.commons.core.LogsCenter;
import homey.logic.commands.Command;
import homey.logic.commands.CommandResult;
import homey.logic.commands.InteractiveCommand;
import homey.logic.commands.exceptions.CommandException;
import homey.logic.parser.AddressBookParser;
import homey.logic.parser.Prefix;
import homey.logic.parser.exceptions.ParseException;
import homey.model.Model;
import homey.model.ReadOnlyAddressBook;
import homey.model.person.Person;
import homey.storage.Storage;
import javafx.collections.ObservableList;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    public static final String CANCEL_COMMAND_WORD = "cancel";
    public static final String MESSAGE_CANCEL_COMMAND = "Command cancelled";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;
    private InteractiveCommand pendingInteractiveCommand;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");
        model.updateMeetingOverdueStatus();

        CommandResult commandResult;

        if (pendingInteractiveCommand != null) {
            commandResult = handleInteractiveResponse(commandText);
        } else {
            Command command = addressBookParser.parseCommand(commandText);

            if (command instanceof InteractiveCommand && ((InteractiveCommand) command).isInteractive()) {
                pendingInteractiveCommand = (InteractiveCommand) command;
            }

            commandResult = command.execute(model);
        }

        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        return commandResult;
    }

    private CommandResult handleInteractiveResponse(String input) throws CommandException, ParseException {
        if (input.trim().equalsIgnoreCase(CANCEL_COMMAND_WORD)) {
            pendingInteractiveCommand = null;
            return new CommandResult(MESSAGE_CANCEL_COMMAND);
        }

        Prefix currentField = pendingInteractiveCommand.getNextMissingField();
        pendingInteractiveCommand.updateField(currentField, input);

        Command command = pendingInteractiveCommand;
        CommandResult result = command.execute(model);

        if (pendingInteractiveCommand.getMissingFields().isEmpty()) {
            pendingInteractiveCommand = null;
        }

        return result;
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return model.getAddressBook();
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public Path getAddressBookFilePath() {
        return model.getAddressBookFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }

    @Override
    public Model getModel() {
        return model;
    }
}
