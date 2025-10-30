package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.parser.CliSyntax.PREFIX_REMARK;
import static java.util.Objects.requireNonNull;

import java.util.Optional;

import homey.commons.core.index.Index;
import homey.commons.exceptions.IllegalValueException;
import homey.logic.commands.RemarkCommand;
import homey.logic.parser.exceptions.ParseException;
import homey.model.person.Remark;


/**
 * Parses input arguments and creates a new {@code RemarkCommand} object.
 */
public class RemarkCommandParser implements Parser<RemarkCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the {@code RemarkCommand}
     * and returns a {@code RemarkCommand} object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public RemarkCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_REMARK);

        Index index = parseIndex(argMultimap);
        validatePrefix(argMultimap);

        String remarkValue = parseRemarkValue(argMultimap);
        Remark remark = new Remark(remarkValue);
        return new RemarkCommand(index, remark);
    }

    /**
     * Parses and validates the index.
     */
    private Index parseIndex(ArgumentMultimap argMultimap) throws ParseException {
        try {
            return ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE), ive);
        }
    }

    /**
     * Ensures only one remark prefix exists.
     */
    private void validatePrefix(ArgumentMultimap argMultimap) throws ParseException {
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_REMARK);
    }

    /**
     * Extracts and validates the remark string.
     */
    private String parseRemarkValue(ArgumentMultimap argMultimap) throws ParseException {
        String remarkValue = extractRemarkValue(argMultimap);
        validateRemarkLength(remarkValue);
        return remarkValue;
    }

    /**
     * Extracts the remark value or throws if prefix is missing.
     */
    private String extractRemarkValue(ArgumentMultimap argMultimap) throws ParseException {
        Optional<String> remarkArg = argMultimap.getValue(PREFIX_REMARK);

        if (remarkArg.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE));
        }

        String remarkValue = remarkArg.get().trim();
        return remarkValue;
    }

    /**
     * Ensures the remark value does not exceed 100 characters.
     */
    private void validateRemarkLength(String remarkValue) throws ParseException {
        if (remarkValue.length() > 100) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    "Remark cannot exceed 100 characters."));
        }
    }
}
