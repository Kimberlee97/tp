package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.parser.CliSyntax.PREFIX_CLIENT;
import static homey.logic.parser.CliSyntax.PREFIX_VENDOR;
import static java.util.Objects.requireNonNull;

import homey.commons.core.index.Index;
import homey.commons.exceptions.IllegalValueException;
import homey.logic.commands.RelationCommand;
import homey.logic.parser.exceptions.ParseException;
import homey.model.tag.Relation;

/**
 * Parses input arguments and creates a new {@code RelationCommand} object.
 */
public class RelationCommandParser implements Parser<RelationCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the {@code RelationCommand}
     * and returns a {@code RelationCommand} object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public RelationCommand parse(String args) throws ParseException {
        requireNonNull(args);
        args = args.toLowerCase();
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_CLIENT, PREFIX_VENDOR);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RelationCommand.MESSAGE_USAGE), ive);
        }

        String relation = "";
        if (argMultimap.getValue(PREFIX_CLIENT).isPresent()) {
            relation = String.valueOf(PREFIX_CLIENT);
        } else if (argMultimap.getValue(PREFIX_VENDOR).isPresent()) {
            relation = String.valueOf(PREFIX_VENDOR);
        }

        try {
            return new RelationCommand(index, new Relation(relation));
        } catch (IllegalArgumentException iae) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RelationCommand.MESSAGE_USAGE), iae);
        }
    }
}
