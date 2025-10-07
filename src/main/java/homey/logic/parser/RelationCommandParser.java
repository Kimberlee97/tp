package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.parser.CliSyntax.PREFIX_CLIENT;
import static homey.logic.parser.CliSyntax.PREFIX_VENDOR;
import static java.util.Objects.requireNonNull;

import homey.commons.core.index.Index;
import homey.commons.exceptions.IllegalValueException;
import homey.logic.commands.RelationCommand;
import homey.logic.parser.exceptions.ParseException;

public class RelationCommandParser {
    public RelationCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args);

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

        return new RelationCommand(index, relation);
    }
}
