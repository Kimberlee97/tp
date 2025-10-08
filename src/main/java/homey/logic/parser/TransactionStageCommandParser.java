package homey.logic.parser;

import homey.commons.core.index.Index;
import homey.commons.exceptions.IllegalValueException;
import homey.logic.commands.TransactionStageCommand;
import homey.logic.parser.exceptions.ParseException;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.parser.CliSyntax.PREFIX_TRANSACTION;
import static java.util.Objects.requireNonNull;

public class TransactionStageCommandParser implements Parser<TransactionStageCommand> {
    public TransactionStageCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_TRANSACTION);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    TransactionStageCommand.MESSAGE_USAGE), ive);
        }

        String stage = argMultimap.getValue(PREFIX_TRANSACTION).orElseThrow(() ->
                new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        TransactionStageCommand.MESSAGE_USAGE))
        );

        return new TransactionStageCommand(index, stage);
    }
}

