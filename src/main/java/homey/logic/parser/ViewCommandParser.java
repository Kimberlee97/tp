package homey.logic.parser;

import static java.util.Objects.requireNonNull;

import homey.commons.core.index.Index;
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
            throw new ParseException(ViewCommand.MESSAGE_USAGE);
        }
        try {
            Index index = ParserUtil.parseIndex(trimmed);
            return new ViewCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(ViewCommand.MESSAGE_USAGE, pe);
        }
    }
}
