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

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_PHONE, PREFIX_EMAIL)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_MEETING);

        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
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

        if (argMultimap.getValue(PREFIX_TRANSACTION).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
        TransactionStage transaction = ParserUtil.parseStage(argMultimap.getValue(PREFIX_TRANSACTION).get());

        // Default relation is client
        Relation relation = new Relation("client");
        if (argMultimap.getValue(PREFIX_RELATION).isPresent()) {
            relation = ParserUtil.parseRelation(argMultimap.getValue(PREFIX_RELATION).get());
        }

        Person person = new Person(name, phone, email, address, relation, transaction, tagList, meeting);
        return new AddCommand(person);
    }


    /**
     * Returns true if all specified prefixes are present (i.e., have a value) in the argument multimap.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
