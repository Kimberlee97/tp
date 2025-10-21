package homey.logic;

import static homey.logic.LogicManager.MESSAGE_CANCEL_COMMAND;
import static homey.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static homey.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static homey.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static homey.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static homey.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static homey.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static homey.logic.commands.CommandTestUtil.TRANSACTION_DESC_PROSPECT;
import static homey.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static homey.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static homey.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static homey.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static homey.logic.commands.CommandTestUtil.VALID_TRANSACTION_PROSPECT;
import static homey.testutil.Assert.assertThrows;
import static homey.testutil.TypicalPersons.AMY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import homey.logic.commands.AddCommand;
import homey.logic.commands.CommandResult;
import homey.logic.commands.InteractiveCommand;
import homey.logic.commands.ListCommand;
import homey.logic.commands.exceptions.CommandException;
import homey.logic.parser.exceptions.ParseException;
import homey.model.Model;
import homey.model.ModelManager;
import homey.model.ReadOnlyAddressBook;
import homey.model.UserPrefs;
import homey.model.person.Person;
import homey.storage.JsonAddressBookStorage;
import homey.storage.JsonUserPrefsStorage;
import homey.storage.StorageManager;
import homey.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with an AddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath)
                    throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + TRANSACTION_DESC_PROSPECT;
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }

    @Test
    public void execute_interactiveAddCommand_success() throws Exception {
        Person expectedPerson = new PersonBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_AMY)
                .withEmail(VALID_EMAIL_AMY)
                .withAddress(VALID_ADDRESS_AMY)
                .withStage(VALID_TRANSACTION_PROSPECT)
                .build();

        // Start with just the name
        String initialCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY;
        CommandResult result = logic.execute(initialCommand);
        assertEquals(AddCommand.MESSAGE_MISSING_PHONE + "\n" + InteractiveCommand.MESSAGE_INTERACTIVE,
                result.getFeedbackToUser());

        // Add phone
        result = logic.execute(VALID_PHONE_AMY);
        assertEquals(AddCommand.MESSAGE_MISSING_EMAIL + "\n" + InteractiveCommand.MESSAGE_INTERACTIVE,
                result.getFeedbackToUser());

        // Add email
        result = logic.execute(VALID_EMAIL_AMY);
        assertEquals(AddCommand.MESSAGE_MISSING_ADDRESS + "\n" + InteractiveCommand.MESSAGE_INTERACTIVE,
                result.getFeedbackToUser());

        // Add address
        result = logic.execute(VALID_ADDRESS_AMY);
        assertEquals(AddCommand.MESSAGE_MISSING_STAGE + "\n" + InteractiveCommand.MESSAGE_INTERACTIVE,
                result.getFeedbackToUser());

        // Add transaction stage
        result = logic.execute(VALID_TRANSACTION_PROSPECT);
        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(expectedPerson)),
                result.getFeedbackToUser());
        assertEquals(expectedPerson, logic.getFilteredPersonList().get(0));
    }

    @Test
    public void execute_interactiveCommandCancel_success() throws Exception {
        // Start interactive command
        String initialCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY;
        CommandResult result = logic.execute(initialCommand);
        assertEquals(AddCommand.MESSAGE_MISSING_PHONE + "\n" + InteractiveCommand.MESSAGE_INTERACTIVE,
                result.getFeedbackToUser());

        // Cancel the command
        result = logic.execute("cancel");
        assertEquals(MESSAGE_CANCEL_COMMAND, result.getFeedbackToUser());

        // Verify no person was added
        assertTrue(logic.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_interactiveCommandInvalidInput_promptsAgain() throws Exception {
        // Start with just the name
        String initialCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY;
        CommandResult result = logic.execute(initialCommand);
        assertEquals(AddCommand.MESSAGE_MISSING_PHONE + "\n" + InteractiveCommand.MESSAGE_INTERACTIVE,
                result.getFeedbackToUser());

        // Try invalid phone number
        assertThrows(ParseException.class, () -> logic.execute("invalid-phone"));

        // Should still prompt for phone
        result = logic.execute(VALID_PHONE_AMY);
        assertEquals(AddCommand.MESSAGE_MISSING_EMAIL + "\n" + InteractiveCommand.MESSAGE_INTERACTIVE,
                result.getFeedbackToUser());
    }
}
