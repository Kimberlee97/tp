package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.parser.CliSyntax.PREFIX_CLIENT;
import static homey.logic.parser.CliSyntax.PREFIX_VENDOR;
import static homey.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import homey.MainApp;
import homey.commons.core.LogsCenter;
import homey.commons.core.index.Index;
import homey.commons.exceptions.IllegalValueException;
import homey.logic.commands.RelationCommand;
import homey.logic.parser.exceptions.ParseException;
import homey.model.tag.Relation;

/**
 * Parses input arguments and creates a new {@code RelationCommand} object.
 */
public class RelationCommandParser implements Parser<RelationCommand> {
    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    private Index index;
    private Relation relation;

    /**
     * Parses the given {@code String} of arguments in the context of the {@code RelationCommand}
     * and returns a {@code RelationCommand} object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public RelationCommand parse(String args) throws ParseException {
        requireNonNull(args);
        args = args.toLowerCase();
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_CLIENT, PREFIX_VENDOR);
        index = parseIndex(argMultimap);
        if (argMultimap.getValue(PREFIX_VENDOR).isEmpty() && argMultimap.getValue(PREFIX_CLIENT).isEmpty()
                || argMultimap.getValue(PREFIX_VENDOR).isPresent() && argMultimap.getValue(PREFIX_CLIENT).isPresent()) {
            relation = ParserUtil.parseRelation("");
        }

        if (argMultimap.getValue(PREFIX_CLIENT).isPresent()
                && argMultimap.getValue(PREFIX_CLIENT).get().isEmpty()) {
            relation = ParserUtil.parseRelation("client");
        } else if (argMultimap.getValue(PREFIX_VENDOR).isPresent()
                && argMultimap.getValue(PREFIX_VENDOR).get().isEmpty()) {
            relation = ParserUtil.parseRelation("vendor");
        } else {
            relation = ParserUtil.parseRelation("");
        }

        logger.fine("Creating Relation Command: " + index.getOneBased() + " " + relation.value);

        return new RelationCommand(index, relation);
    }

    private Index parseIndex(ArgumentMultimap argMultimap) throws ParseException {
        String preamble = argMultimap.getPreamble().trim();
        if (preamble.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RelationCommand.MESSAGE_USAGE));
        }

        String[] parts = preamble.split("\\s+");
        if (parts.length > 1) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RelationCommand.MESSAGE_USAGE));
        }

        try {
            return ParserUtil.parseIndex(parts[0]);
        } catch (IllegalValueException ive) {
            if (ive.getMessage().equals(MESSAGE_INVALID_INDEX)) {
                throw new ParseException(ive.getMessage(), ive);
            }
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, RelationCommand.MESSAGE_USAGE), ive);
        }
    }
}
