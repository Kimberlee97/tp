package homey.logic.commands;

import static homey.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static homey.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static homey.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static homey.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static homey.logic.commands.CommandTestUtil.VALID_TRANSACTION_PROSPECT;
import static homey.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static homey.logic.parser.CliSyntax.PREFIX_EMAIL;
import static homey.logic.parser.CliSyntax.PREFIX_NAME;
import static homey.logic.parser.CliSyntax.PREFIX_PHONE;
import static homey.logic.parser.CliSyntax.PREFIX_TRANSACTION;
import static homey.testutil.Assert.assertThrows;
import static homey.testutil.TypicalPersons.BOB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import homey.logic.parser.Prefix;
import homey.logic.parser.exceptions.ParseException;
import homey.model.Model;
import homey.model.ModelManager;
import homey.model.person.Person;
import homey.testutil.PersonBuilder;

public class AddCommandInteractiveTest {

    private Model model = new ModelManager();

    @Test
    public void execute_interactiveMode_promptsForMissingFields() throws Exception {
        Person partialPerson = new PersonBuilder().build();
        Map<Prefix, String> missingFields = new HashMap<>();
        missingFields.put(PREFIX_NAME, "");
        missingFields.put(PREFIX_PHONE, "");
        missingFields.put(PREFIX_EMAIL, "");
        missingFields.put(PREFIX_ADDRESS, "");
        missingFields.put(PREFIX_TRANSACTION, "");
        AddCommand addCommand = new AddCommand(partialPerson, true, missingFields);

        // Prompt for name
        CommandResult result = addCommand.execute(model);
        assertTrue(addCommand.isInteractive());
        assertEquals(AddCommand.MESSAGE_MISSING_NAME + "\n" + InteractiveCommand.MESSAGE_INTERACTIVE,
                result.getFeedbackToUser());

        // Update name field
        addCommand.updateField(PREFIX_NAME, VALID_NAME_BOB);
        result = addCommand.execute(model);
        assertTrue(addCommand.isInteractive());
        assertEquals(AddCommand.MESSAGE_MISSING_PHONE + "\n" + InteractiveCommand.MESSAGE_INTERACTIVE,
                result.getFeedbackToUser());

        // Update phone field
        addCommand.updateField(PREFIX_PHONE, VALID_PHONE_BOB);
        result = addCommand.execute(model);
        assertTrue(addCommand.isInteractive());
        assertEquals(AddCommand.MESSAGE_MISSING_EMAIL + "\n" + InteractiveCommand.MESSAGE_INTERACTIVE,
                result.getFeedbackToUser());

        // Update email field
        addCommand.updateField(PREFIX_EMAIL, VALID_EMAIL_BOB);
        result = addCommand.execute(model);
        assertTrue(addCommand.isInteractive());
        assertEquals(AddCommand.MESSAGE_MISSING_ADDRESS + "\n" + InteractiveCommand.MESSAGE_INTERACTIVE,
                result.getFeedbackToUser());

        // Update address field
        addCommand.updateField(PREFIX_ADDRESS, VALID_ADDRESS_BOB);
        result = addCommand.execute(model);
        assertTrue(addCommand.isInteractive());
        assertEquals(AddCommand.MESSAGE_MISSING_STAGE + "\n" + InteractiveCommand.MESSAGE_INTERACTIVE,
                result.getFeedbackToUser());

        // Update transaction stage field
        addCommand.updateField(PREFIX_TRANSACTION, VALID_TRANSACTION_PROSPECT);
        result = addCommand.execute(model);
        assertFalse(addCommand.isInteractive());
        assertTrue(model.hasPerson(new PersonBuilder(BOB).withTags().build()));
    }

    @Test
    public void execute_invalidInput_remainsInInteractiveMode() throws Exception {
        Person partialPerson = new PersonBuilder().withName(VALID_NAME_BOB).build();
        Map<Prefix, String> missingFields = new HashMap<>();
        missingFields.put(PREFIX_PHONE, "");
        AddCommand addCommand = new AddCommand(partialPerson, true, missingFields);

        // Invalid phone number should keep command in interactive mode
        assertThrows(ParseException.class, () ->
                addCommand.updateField(PREFIX_PHONE, "invalid"));

        CommandResult result = addCommand.execute(model);
        assertTrue(addCommand.isInteractive());
        assertEquals(AddCommand.MESSAGE_MISSING_PHONE + "\n" + InteractiveCommand.MESSAGE_INTERACTIVE,
                result.getFeedbackToUser());
    }
}
