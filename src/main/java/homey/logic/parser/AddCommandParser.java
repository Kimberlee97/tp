package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static homey.logic.parser.CliSyntax.PREFIX_EMAIL;
import static homey.logic.parser.CliSyntax.PREFIX_MEETING;
import static homey.logic.parser.CliSyntax.PREFIX_NAME;
import static homey.logic.parser.CliSyntax.PREFIX_PHONE;
import static homey.logic.parser.CliSyntax.PREFIX_RELATION;
import static homey.logic.parser.CliSyntax.PREFIX_REMARK;
import static homey.logic.parser.CliSyntax.PREFIX_TAG;
import static homey.logic.parser.CliSyntax.PREFIX_TRANSACTION;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import homey.logic.commands.AddCommand;
import homey.logic.parser.exceptions.ParseException;
import homey.model.person.Address;
import homey.model.person.Email;
import homey.model.person.Meeting;
import homey.model.person.Name;
import homey.model.person.Person;
import homey.model.person.Phone;
import homey.model.person.PlaceholderPerson;
import homey.model.person.Remark;
import homey.model.tag.Relation;
import homey.model.tag.Tag;
import homey.model.tag.TransactionStage;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the {@link AddCommand}
     * and returns an {@link AddCommand} object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                        PREFIX_RELATION, PREFIX_TRANSACTION, PREFIX_REMARK, PREFIX_TAG, PREFIX_MEETING);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_PHONE, PREFIX_EMAIL,
                PREFIX_TRANSACTION)
                || !argMultimap.getPreamble().isEmpty()) {

            // Check for missing required fields
            Map<Prefix, String> missingFields = new HashMap<>();
            if (argMultimap.getValue(PREFIX_NAME).isEmpty()) {
                missingFields.put(PREFIX_NAME, "");
            }
            if (argMultimap.getValue(PREFIX_PHONE).isEmpty()) {
                missingFields.put(PREFIX_PHONE, "");
            }
            if (argMultimap.getValue(PREFIX_EMAIL).isEmpty()) {
                missingFields.put(PREFIX_EMAIL, "");
            }
            if (argMultimap.getValue(PREFIX_ADDRESS).isEmpty()) {
                missingFields.put(PREFIX_ADDRESS, "");
            }
            if (argMultimap.getValue(PREFIX_TRANSACTION).isEmpty()) {
                missingFields.put(PREFIX_TRANSACTION, "");
            }

            // If there are missing mandatory fields, create a partial Person and return interactive command
            if (!missingFields.isEmpty()) {
                Person partialPerson = createPartialPerson(argMultimap);
                return new AddCommand(partialPerson, true, missingFields);
            }

            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        return new AddCommand(createCompletePerson(argMultimap));
    }


    /**
     * Returns true if all specified prefixes are present (i.e., have a value) in the argument multimap.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    private Person createCompletePerson(ArgumentMultimap argMultimap) throws ParseException {
        verifyFields(argMultimap);
        return createPersonFromArguments(argMultimap, false);
    }

    private Person createPartialPerson(ArgumentMultimap argMultimap) throws ParseException {
        verifyFields(argMultimap);
        return createPersonFromArguments(argMultimap, true);
    }

    /**
     * Verifies no duplicate prefixes in the argument multimap.
     */
    private void verifyFields(ArgumentMultimap argMultimap) throws ParseException {
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_RELATION, PREFIX_REMARK, PREFIX_TRANSACTION, PREFIX_MEETING);
    }

    /**
     * Creates a Person object from the given arguments.
     * @param argMultimap Contains the field values
     * @param allowPlaceholders If true, uses placeholder values for missing fields
     * @return Person object with the specified or placeholder values
     * @throws ParseException if there are invalid field values
     */
    private Person createPersonFromArguments(ArgumentMultimap argMultimap, boolean allowPlaceholders)
            throws ParseException {

        // Parse name
        Name name = parseName(argMultimap, allowPlaceholders);

        // Parse phone
        Phone phone = parsePhone(argMultimap, allowPlaceholders);

        // Parse email
        Email email = parseEmail(argMultimap, allowPlaceholders);

        // Parse address
        Address address = parseAddress(argMultimap, allowPlaceholders);

        // Parse transaction stage
        TransactionStage transaction = parseStage(argMultimap, allowPlaceholders);

        // Parse tags
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        // Parse meeting (optional)
        Optional<Meeting> meeting = parseMeeting(argMultimap);

        // Parse relation (optional, defaults to client)
        Relation relation = parseRelation(argMultimap);

        // Parse remark (optional, defaults to empty string)
        Remark remark = parseRemark(argMultimap);

        return new Person(name, phone, email, address, relation, transaction, remark, tagList, meeting);
    }

    /**
     * Parses the name field from the argument multimap with placeholders if missing and allowed.
     */
    private Name parseName(ArgumentMultimap argMultimap, boolean allowPlaceholders) throws ParseException {
        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            return ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        } else if (allowPlaceholders) {
            return PlaceholderPerson.PLACEHOLDER_NAME;
        } else {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses the phone field from the argument multimap with placeholders if missing and allowed.
     */
    private Phone parsePhone(ArgumentMultimap argMultimap, boolean allowPlaceholders) throws ParseException {
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            return ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        } else if (allowPlaceholders) {
            return PlaceholderPerson.PLACEHOLDER_PHONE;
        } else {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses the email field from the argument multimap with placeholders if missing and allowed.
     */
    private Email parseEmail(ArgumentMultimap argMultimap, boolean allowPlaceholders) throws ParseException {
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            return ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        } else if (allowPlaceholders) {
            return PlaceholderPerson.PLACEHOLDER_EMAIL;
        } else {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses the address field from the argument multimap with placeholders if missing and allowed.
     */
    private Address parseAddress(ArgumentMultimap argMultimap, boolean allowPlaceholders) throws ParseException {
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            return ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
        } else if (allowPlaceholders) {
            return PlaceholderPerson.PLACEHOLDER_ADDRESS;
        } else {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses the transaction stage field from the argument multimap with placeholders if missing and allowed.
     */
    private TransactionStage parseStage(ArgumentMultimap argMultimap, boolean allowPlaceholders) throws ParseException {
        if (argMultimap.getValue(PREFIX_TRANSACTION).isPresent()) {
            return ParserUtil.parseStage(argMultimap.getValue(PREFIX_TRANSACTION).get());
        } else if (allowPlaceholders) {
            return PlaceholderPerson.PLACEHOLDER_TRANSACTION;
        } else {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses the meeting field from the argument multimap.
     */
    private Optional<Meeting> parseMeeting(ArgumentMultimap argMultimap) throws ParseException {
        if (argMultimap.getValue(PREFIX_MEETING).isPresent()) {
            String meetingValue = argMultimap.getValue(PREFIX_MEETING).get().trim();
            if (!meetingValue.isEmpty()) {
                if (!Meeting.isValidMeeting(meetingValue)) {
                    throw new ParseException(Meeting.MESSAGE_CONSTRAINTS);
                }
                return Optional.of(new Meeting(meetingValue));
            }
        }
        return Optional.empty();
    }

    /**
     * Parses the relation field from the argument multimap.
     */
    private Relation parseRelation(ArgumentMultimap argMultimap) throws ParseException {
        if (argMultimap.getValue(PREFIX_RELATION).isPresent()) {
            return ParserUtil.parseRelation(argMultimap.getValue(PREFIX_RELATION).get());
        } else {
            return new Relation("client");
        }
    }

    private Remark parseRemark(ArgumentMultimap argMultimap) throws ParseException {
        if (argMultimap.getValue(PREFIX_REMARK).isPresent()) {
            return ParserUtil.parseRemark(argMultimap.getValue(PREFIX_REMARK).get());
        } else {
            return new Remark("");
        }
    }

    /**
     * Updates the partial person with a new field value.
     * @param person Current partial person
     * @param prefix Prefix of the field to update
     * @param value New value for the field
     * @return Updated Person object
     * @throws ParseException if the value is invalid for the given field
     */
    public static Person updatePersonField(Person person, Prefix prefix, String value) throws ParseException {
        switch (prefix.toString()) {
        case "n/":
            return new Person(
                    ParserUtil.parseName(value),
                    person.getPhone(),
                    person.getEmail(),
                    person.getAddress(),
                    person.getRelation(),
                    person.getStage(),
                    person.getRemark(),
                    person.getTags(),
                    person.getMeeting()
            );
        case "p/":
            return new Person(
                    person.getName(),
                    ParserUtil.parsePhone(value),
                    person.getEmail(),
                    person.getAddress(),
                    person.getRelation(),
                    person.getStage(),
                    person.getRemark(),
                    person.getTags(),
                    person.getMeeting()
            );
        case "e/":
            return new Person(
                    person.getName(),
                    person.getPhone(),
                    ParserUtil.parseEmail(value),
                    person.getAddress(),
                    person.getRelation(),
                    person.getStage(),
                    person.getRemark(),
                    person.getTags(),
                    person.getMeeting()
            );
        case "a/":
            return new Person(
                    person.getName(),
                    person.getPhone(),
                    person.getEmail(),
                    ParserUtil.parseAddress(value),
                    person.getRelation(),
                    person.getStage(),
                    person.getRemark(),
                    person.getTags(),
                    person.getMeeting()
            );
        case "s/":
            return new Person(
                    person.getName(),
                    person.getPhone(),
                    person.getEmail(),
                    person.getAddress(),
                    person.getRelation(),
                    ParserUtil.parseStage(value),
                    person.getRemark(),
                    person.getTags(),
                    person.getMeeting()
            );
        case "rm/":
            return new Person(
                    person.getName(),
                    person.getPhone(),
                    person.getEmail(),
                    person.getAddress(),
                    person.getRelation(),
                    ParserUtil.parseStage(value),
                    ParserUtil.parseRemark(value),
                    person.getTags(),
                    person.getMeeting()
            );
        default:
            // Unknown field prefix
            assert false : "Unknown field prefix: " + prefix;
            return null;
        }
    }
}
