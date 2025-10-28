package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static homey.logic.parser.CliSyntax.PREFIX_TRANSACTION;
import static java.util.Objects.requireNonNull;

import homey.commons.core.index.Index;
import homey.commons.exceptions.IllegalValueException;
import homey.logic.commands.TransactionStageCommand;
import homey.logic.parser.exceptions.ParseException;
import homey.model.tag.TransactionStage;


/**
 * Parses input arguments and creates a new {@code TransactionStageCommand} object.
 */
public class TransactionStageCommandParser implements Parser<TransactionStageCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the {@code TransactionStageCommand}
     * and returns a {@code TransactionStageCommand} object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public TransactionStageCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_TRANSACTION);
        validatePrefix(argMultimap);
        Index index = parseIndex(argMultimap);
        TransactionStage stage = parseStage(argMultimap);
        return new TransactionStageCommand(index, stage);
    }

    /**
     * Parses and validates the index.
     */
    private Index parseIndex(ArgumentMultimap argMultimap) throws ParseException {
        try {
            return ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, TransactionStageCommand.MESSAGE_USAGE), ive);
        }
    }

    /**
     * Ensures only one transaction prefix exists.
     */
    private void validatePrefix(ArgumentMultimap argMultimap) throws ParseException {
        if (argMultimap.getAllValues(PREFIX_TRANSACTION).size() > 1) {
            throw new ParseException(
                    getErrorMessageForDuplicatePrefixes(PREFIX_TRANSACTION));
        } else if (argMultimap.getAllValues(PREFIX_TRANSACTION).isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, TransactionStageCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Validates the input stage and returns the corresponding {@code TransactionStage} object.
     */
    private TransactionStage parseStage(ArgumentMultimap argMultimap) throws ParseException {
        String raw = argMultimap.getValue(PREFIX_TRANSACTION).orElseThrow(() ->
                new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, TransactionStageCommand.MESSAGE_USAGE)
                )
        );

        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, "Transaction stage cannot be empty.")
            );
        }
        TransactionStage stage = ParserUtil.parseStage(trimmed);
        return stage;
    }
}

