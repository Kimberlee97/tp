package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static java.util.Objects.requireNonNull;

import homey.commons.core.index.Index;
import homey.logic.Messages;
import homey.logic.commands.ViewCommand;
import homey.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ViewCommand object
 */
public class ViewCommandParser implements Parser<ViewCommand> {
    @Override
    public ViewCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmed = args.trim();
        if (trimmed.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE));
        }
        try {
            Index index = ParserUtil.parseIndex(trimmed);
            if (index.getOneBased() < 1) {
                throw new ParseException(Messages.MESSAGE_INVALID_PERSON_LOWER_BOUND_INDEX);
            }
            return new ViewCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(Messages.MESSAGE_INVALID_PERSON_LOWER_BOUND_INDEX
                    + "\n" + ViewCommand.MESSAGE_USAGE, pe);
        }
    }
}
