package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static homey.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static homey.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static homey.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static homey.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static homey.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static homey.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static homey.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static homey.logic.commands.CommandTestUtil.INVALID_RELATION_DESC;
import static homey.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static homey.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static homey.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static homey.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static homey.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static homey.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static homey.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static homey.logic.commands.CommandTestUtil.RELATION_DESC_CLIENT;
import static homey.logic.commands.CommandTestUtil.RELATION_DESC_VENDOR;
import static homey.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static homey.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static homey.logic.commands.CommandTestUtil.TRANSACTION_DESC_PROSPECT;
import static homey.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static homey.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static homey.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static homey.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static homey.logic.commands.CommandTestUtil.VALID_RELATION_VENDOR;
import static homey.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static homey.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static homey.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static homey.logic.parser.CliSyntax.PREFIX_EMAIL;
import static homey.logic.parser.CliSyntax.PREFIX_NAME;
import static homey.logic.parser.CliSyntax.PREFIX_PHONE;
import static homey.logic.parser.CliSyntax.PREFIX_RELATION;
import static homey.logic.parser.CliSyntax.PREFIX_TRANSACTION;
import static homey.logic.parser.CommandParserTestUtil.assertParseFailure;
import static homey.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static homey.testutil.Assert.assertThrows;
import static homey.testutil.TypicalPersons.AMY;
import static homey.testutil.TypicalPersons.BOB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import homey.logic.Messages;
import homey.logic.commands.AddCommand;
import homey.logic.commands.HelpCommand;
import homey.model.person.Address;
import homey.model.person.Email;
import homey.model.person.Name;
import homey.model.person.Person;
import homey.model.person.Phone;
import homey.model.tag.Relation;
import homey.model.tag.Tag;
import homey.testutil.PersonBuilder;

public class AddCommandParserTest {
    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Person expectedPerson = new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND).build();

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + TRANSACTION_DESC_PROSPECT + TAG_DESC_FRIEND, new AddCommand(expectedPerson));


        // multiple tags - all accepted
        Person expectedPersonMultipleTags = new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND)
                .build();
        assertParseSuccess(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TRANSACTION_DESC_PROSPECT
                        + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                new AddCommand(expectedPersonMultipleTags));

        // with relation tag "vendor"
        Person expectedPersonRelation = new PersonBuilder(BOB).withRelation(VALID_RELATION_VENDOR)
                .build();
        assertParseSuccess(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + RELATION_DESC_VENDOR
                        + TRANSACTION_DESC_PROSPECT + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                new AddCommand(expectedPersonRelation));
    }

    @Test
    public void parse_repeatedNonTagValue_failure() {
        String validExpectedPersonString = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + TRANSACTION_DESC_PROSPECT + TAG_DESC_FRIEND;

        // multiple names
        assertParseFailure(parser, NAME_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // multiple phones
        assertParseFailure(parser, PHONE_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple emails
        assertParseFailure(parser, EMAIL_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // multiple addresses
        assertParseFailure(parser, ADDRESS_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // multiple relations
        assertParseFailure(parser, RELATION_DESC_VENDOR + RELATION_DESC_CLIENT + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_RELATION));

        // multiple fields repeated
        assertParseFailure(parser,
                validExpectedPersonString + PHONE_DESC_AMY + EMAIL_DESC_AMY + NAME_DESC_AMY + ADDRESS_DESC_AMY
                        + TRANSACTION_DESC_PROSPECT + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_ADDRESS, PREFIX_EMAIL, PREFIX_PHONE));

        // invalid value followed by valid value

        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        assertParseFailure(parser, INVALID_EMAIL_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, INVALID_PHONE_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        assertParseFailure(parser, INVALID_ADDRESS_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // invalid relation
        assertParseFailure(parser, INVALID_RELATION_DESC + validExpectedPersonString + RELATION_DESC_VENDOR,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_RELATION));

        // valid value followed by invalid value

        // invalid name
        assertParseFailure(parser, validExpectedPersonString + INVALID_NAME_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        assertParseFailure(parser, validExpectedPersonString + INVALID_EMAIL_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, validExpectedPersonString + INVALID_PHONE_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        assertParseFailure(parser, validExpectedPersonString + INVALID_ADDRESS_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // invalid relation
        assertParseFailure(parser, RELATION_DESC_CLIENT + validExpectedPersonString + INVALID_RELATION_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_RELATION));
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // zero tags
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        assertParseSuccess(parser, NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                        + TRANSACTION_DESC_PROSPECT,
                new AddCommand(expectedPerson));

        // zero relations
        Person expectedPersonRelation = new PersonBuilder(AMY).build();
        assertParseSuccess(parser, NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                        + TRANSACTION_DESC_PROSPECT + TAG_DESC_FRIEND,
                new AddCommand(expectedPersonRelation));
    }

    @Test
    public void parse_compulsoryFieldMissing_returnsInteractiveCommand() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        // missing name prefix
        Person partialPerson1 = new PersonBuilder()
                .withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_BOB)
                .withStage("prospect")
                .build();
        assertParseSuccess(parser, PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                        + TRANSACTION_DESC_PROSPECT,
                new AddCommand(partialPerson1, true, Map.of(PREFIX_NAME, "")));

        // missing phone prefix
        Person partialPerson2 = new PersonBuilder()
                .withName(VALID_NAME_BOB)
                .withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_BOB)
                .withStage("prospect")
                .build();
        assertParseSuccess(parser, NAME_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                        + TRANSACTION_DESC_PROSPECT,
                new AddCommand(partialPerson2, true, Map.of(PREFIX_PHONE, "")));

        // missing email prefix
        Person partialPerson3 = new PersonBuilder()
                .withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB)
                .withAddress(VALID_ADDRESS_BOB)
                .withStage("prospect")
                .build();
        assertParseSuccess(parser, NAME_DESC_BOB + PHONE_DESC_BOB + ADDRESS_DESC_BOB
                        + TRANSACTION_DESC_PROSPECT,
                new AddCommand(partialPerson3, true, Map.of(PREFIX_EMAIL, "")));

        // missing address prefix
        Person partialPerson4 = new PersonBuilder()
                .withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_BOB)
                .withStage("prospect")
                .build();
        assertParseSuccess(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + TRANSACTION_DESC_PROSPECT,
                new AddCommand(partialPerson4, true, Map.of(PREFIX_ADDRESS, "")));

        // all prefixes missing
        Person emptyPerson = new PersonBuilder().build();
        Map<Prefix, String> allMissingFields = Map.of(
                PREFIX_NAME, "",
                PREFIX_PHONE, "",
                PREFIX_EMAIL, "",
                PREFIX_ADDRESS, ""
        );
        assertParseSuccess(parser, TRANSACTION_DESC_PROSPECT,
                new AddCommand(emptyPerson, true, allMissingFields));
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + TRANSACTION_DESC_PROSPECT + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, Name.MESSAGE_CONSTRAINTS);

        // invalid phone
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_PHONE_DESC + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + TRANSACTION_DESC_PROSPECT + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, Phone.MESSAGE_CONSTRAINTS);

        // invalid email
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + INVALID_EMAIL_DESC + ADDRESS_DESC_BOB
                + TRANSACTION_DESC_PROSPECT + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, Email.MESSAGE_CONSTRAINTS);

        // invalid address
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + INVALID_ADDRESS_DESC
                + TRANSACTION_DESC_PROSPECT + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, Address.MESSAGE_CONSTRAINTS);

        // invalid relation
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + INVALID_RELATION_DESC + TRANSACTION_DESC_PROSPECT + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                Relation.MESSAGE_CONSTRAINTS);

        // invalid tag
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + TRANSACTION_DESC_PROSPECT + INVALID_TAG_DESC + VALID_TAG_FRIEND, Tag.MESSAGE_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB + EMAIL_DESC_BOB + INVALID_ADDRESS_DESC
                + TRANSACTION_DESC_PROSPECT, Name.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + TRANSACTION_DESC_PROSPECT + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingCompulsoryAndInvalidFields_failure() {
        // missing address, invalid email
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + INVALID_EMAIL_DESC
                        + TRANSACTION_DESC_PROSPECT, Email.MESSAGE_CONSTRAINTS);

        // missing email, invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB + ADDRESS_DESC_BOB
                + TRANSACTION_DESC_PROSPECT, Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_onlyOptionalFields_returnsInteractiveCommand() {
        // only relation, stage and tags provided
        Person emptyPerson = new PersonBuilder()
                .withRelation(VALID_RELATION_VENDOR)
                .withTags(VALID_TAG_FRIEND)
                .build();
        Map<Prefix, String> missingFields = Map.of(
                PREFIX_NAME, "",
                PREFIX_PHONE, "",
                PREFIX_EMAIL, "",
                PREFIX_ADDRESS, "",
                PREFIX_TRANSACTION, ""
        );
        assertParseSuccess(parser, TAG_DESC_FRIEND + RELATION_DESC_VENDOR,
                new AddCommand(emptyPerson, true, missingFields));
    }

    @Test
    public void updatePersonField_unknownPrefix_throwsAssertionError() {
        Person person = new PersonBuilder().build();
        Prefix unknownPrefix = new Prefix("unknown/");

        // Test that assertion is triggered
        assertThrows(AssertionError.class, () ->
                AddCommandParser.updatePersonField(person, unknownPrefix, "any value"));
    }

    @Test
    public void parse_helpNoTopic_returnsHelpCommand() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        HelpCommand cmd = (HelpCommand) parser.parseCommand("help");
        assertEquals(new HelpCommand(), cmd);
    }

    @Test
    public void parse_helpWithTopic_returnsHelpCommand() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        HelpCommand cmd = (HelpCommand) parser.parseCommand("help add");
        assertEquals(new HelpCommand("add"), cmd);
    }
}
