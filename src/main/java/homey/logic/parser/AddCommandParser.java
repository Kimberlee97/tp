package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static homey.logic.parser.CliSyntax.PREFIX_EMAIL;
import static homey.logic.parser.CliSyntax.PREFIX_MEETING;
import static homey.logic.parser.CliSyntax.PREFIX_NAME;
import static homey.logic.parser.CliSyntax.PREFIX_PHONE;
import static homey.logic.parser.CliSyntax.PREFIX_RELATION;
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
                        PREFIX_RELATION, PREFIX_TRANSACTION, PREFIX_TAG, PREFIX_MEETING);

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

            // should not reach here
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
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_RELATION, PREFIX_MEETING);

        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
        TransactionStage transaction = ParserUtil.parseStage(argMultimap.getValue(PREFIX_TRANSACTION).get());
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        Optional<Meeting> meeting = Optional.empty();
        if (argMultimap.getValue(PREFIX_MEETING).isPresent()) {
            String meetingValue = argMultimap.getValue(PREFIX_MEETING).get().trim();
            if (!meetingValue.isEmpty()) {
                if (!Meeting.isValidMeeting(meetingValue)) {
                    throw new ParseException(Meeting.MESSAGE_CONSTRAINTS);
                }
                meeting = Optional.of(new Meeting(meetingValue));
            }
        }

        // Default relation is client
        Relation relation = new Relation("client");
        if (argMultimap.getValue(PREFIX_RELATION).isPresent()) {
            relation = ParserUtil.parseRelation(argMultimap.getValue(PREFIX_RELATION).get());
        }

        return new Person(name, phone, email, address, relation, transaction, tagList, meeting);
    }

    private Person createPartialPerson(ArgumentMultimap argMultimap) throws ParseException {
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_RELATION, PREFIX_MEETING);

        // Create a Person with available fields, using placeholder values for missing ones
        Name name = argMultimap.getValue(PREFIX_NAME).isPresent()
                ? ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get())
                : PlaceholderPerson.PLACEHOLDER_NAME;
        Phone phone = argMultimap.getValue(PREFIX_PHONE).isPresent()
                ? ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get())
                : PlaceholderPerson.PLACEHOLDER_PHONE;
        Email email = argMultimap.getValue(PREFIX_EMAIL).isPresent()
                ? ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get())
                : PlaceholderPerson.PLACEHOLDER_EMAIL;
        Address address = argMultimap.getValue(PREFIX_ADDRESS).isPresent()
                ? ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get())
                : PlaceholderPerson.PLACEHOLDER_ADDRESS;
        TransactionStage transaction = argMultimap.getValue(PREFIX_TRANSACTION).isPresent()
                ? ParserUtil.parseStage(argMultimap.getValue(PREFIX_TRANSACTION).get())
                : PlaceholderPerson.PLACEHOLDER_TRANSACTION;
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        Optional<Meeting> meeting = Optional.empty();
        if (argMultimap.getValue(PREFIX_MEETING).isPresent()) {
            String meetingValue = argMultimap.getValue(PREFIX_MEETING).get().trim();
            if (!meetingValue.isEmpty()) {
                if (!Meeting.isValidMeeting(meetingValue)) {
                    throw new ParseException(Meeting.MESSAGE_CONSTRAINTS);
                }
                meeting = Optional.of(new Meeting(meetingValue));
            }
        }

        // Default relation is client
        Relation relation = new Relation("client");
        if (argMultimap.getValue(PREFIX_RELATION).isPresent()) {
            relation = ParserUtil.parseRelation(argMultimap.getValue(PREFIX_RELATION).get());
        }

        return new Person(name, phone, email, address, relation, transaction, tagList, meeting);
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
