package homey.logic.commands;

import static homey.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static homey.logic.parser.CliSyntax.PREFIX_EMAIL;
import static homey.logic.parser.CliSyntax.PREFIX_PHONE;
import static homey.testutil.Assert.assertThrows;
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
        Person partialPerson = new PersonBuilder().withName("Bob").build();
        Map<Prefix, String> missingFields = new HashMap<>();
        missingFields.put(PREFIX_PHONE, "");
        missingFields.put(PREFIX_EMAIL, "");
        missingFields.put(PREFIX_ADDRESS, "");
        AddCommand addCommand = new AddCommand(partialPerson, true, missingFields);

        // First execution should prompt for phone
        CommandResult result = addCommand.execute(model);
        assertTrue(addCommand.isInteractive());
        assertEquals(AddCommand.MESSAGE_MISSING_PHONE + "\n" + InteractiveCommand.MESSAGE_INTERACTIVE,
                result.getFeedbackToUser());

        // Update phone field
        addCommand.updateField(PREFIX_PHONE, "91234567");
        result = addCommand.execute(model);
        assertTrue(addCommand.isInteractive());
        assertEquals(AddCommand.MESSAGE_MISSING_EMAIL + "\n" + InteractiveCommand.MESSAGE_INTERACTIVE,
                result.getFeedbackToUser());

        // Update email field
        addCommand.updateField(PREFIX_EMAIL, "bob@example.com");
        result = addCommand.execute(model);
        assertTrue(addCommand.isInteractive());
        assertEquals(AddCommand.MESSAGE_MISSING_ADDRESS + "\n" + InteractiveCommand.MESSAGE_INTERACTIVE,
                result.getFeedbackToUser());

        // Final field update should complete the command
        addCommand.updateField(PREFIX_ADDRESS, "123 Main St");
        result = addCommand.execute(model);
        assertFalse(addCommand.isInteractive());
        assertTrue(model.hasPerson(partialPerson));
    }

    @Test
    public void execute_invalidInput_remainsInInteractiveMode() throws Exception {
        Person partialPerson = new PersonBuilder().withName("Bob").build();
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
