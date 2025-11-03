package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.Messages.MESSAGE_SINGLE_KEYWORD_ONLY;
import static homey.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static homey.logic.parser.CliSyntax.PREFIX_RELATION;
import static homey.logic.parser.CliSyntax.PREFIX_TAG;
import static homey.logic.parser.CliSyntax.PREFIX_TRANSACTION;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import homey.logic.commands.FindCommand;
import homey.logic.parser.exceptions.ParseException;
import homey.model.person.AddressContainsKeywordsPredicate;
import homey.model.person.NameContainsKeywordsPredicate;
import homey.model.person.RelationContainsKeywordPredicate;
import homey.model.person.TagContainsKeywordsPredicate;
import homey.model.person.TransactionContainsKeywordPredicate;

/**
 * Parses input arguments and creates a new {@link FindCommand}.
 *
 * <p>Supported forms:
 * <ul>
 *   <li>Name search (default): {@code find KEYWORD [MORE_KEYWORDS]}</li>
 *   <li>Address search: {@code find a/KEYWORD [MORE_KEYWORDS]}</li>
 *   <li>Tag search: {@code find t/KEYWORD [MORE_KEYWORDS]}</li>
 *   <li>Relation search: {@code find r/KEYWORD}</li>
 *   <li>Transaction Stage search: {@code find s/KEYWORD}</li>
 * </ul>
 *
 * <p>When {@code a/} is provided without any keywords (e.g., {@code find a/}, {@code find t/}),
 * this parser returns an error showing the address-specific usage only.
 */
public class FindCommandParser implements Parser<FindCommand> {

    private static final Set<String> VALID_RELATIONS = Set.of("client", "vendor");
    private static final String RELATION_ERROR_MESSAGE =
            "Invalid relation. Only 'client' or 'vendor' are allowed";

    private static final Set<String> VALID_TRANSACTIONS = Set.of("prospect", "negotiating", "closed");
    private static final String TRANSACTION_ERROR_MESSAGE =
            "Invalid transaction stage. Only 'prospect' or 'negotiating' or 'closed' are allowed";
    private static final String TAG_ERROR_MESSAGE =
            "Invalid keyword. Tags can only contain alphanumeric characters";

    private static final Set<String> VALID_PREFIXES = Set.of(
            PREFIX_ADDRESS.toString(),
            PREFIX_TAG.toString(),
            PREFIX_RELATION.toString(),
            PREFIX_TRANSACTION.toString()
    );

    @Override
    public FindCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = validateAndTrim(args);
        ArgumentMultimap argMultimap = tokenizeArguments(args);
        validatePrefixes(argMultimap);

        if (trimmedArgs.startsWith(PREFIX_ADDRESS.toString())) {
            String afterPrefix = extractAfterPrefix(trimmedArgs, PREFIX_ADDRESS.toString());
            return parseAddress(afterPrefix);
        }

        if (trimmedArgs.startsWith(PREFIX_TAG.toString())) {
            String afterPrefix = extractAfterPrefix(trimmedArgs, PREFIX_TAG.toString());
            return parseTag(afterPrefix);
        }

        if (trimmedArgs.startsWith(PREFIX_RELATION.toString())) {
            String afterPrefix = extractAfterPrefix(trimmedArgs, PREFIX_RELATION.toString());
            return parseRelation(afterPrefix);
        }

        if (trimmedArgs.startsWith(PREFIX_TRANSACTION.toString())) {
            String afterPrefix = extractAfterPrefix(trimmedArgs, PREFIX_TRANSACTION.toString());
            return parseTransaction(afterPrefix);
        }

        return parseName(trimmedArgs);
    }

    /**
     * Builds the address-only usage string for {@code find a/KEYWORD [MORE_KEYWORDS]}.
     */
    private static String buildAddressOnlyUsage() {
        return "Address: " + FindCommand.COMMAND_WORD + " " + PREFIX_ADDRESS + "KEYWORD [MORE_KEYWORDS]";
    }

    /**
     * Builds the tag-only usage string for {@code find t/KEYWORD [MORE_KEYWORDS]}.
     */
    private static String buildTagOnlyUsage() {
        return "Tags: " + FindCommand.COMMAND_WORD + " " + PREFIX_TAG + "KEYWORD [MORE_KEYWORDS]";
    }

    /**
     * Builds the transaction-only usage string for {@code find s/KEYWORD}.
     */
    private static String buildTransactionOnlyUsage() {
        return "Transaction stage: " + FindCommand.COMMAND_WORD + " " + PREFIX_TRANSACTION + "KEYWORD";
    }

    /**
     * Builds the relation-only usage string for {@code find r/KEYWORD}
     */
    private static String buildRelationOnlyUsage() {
        return "Relation: " + FindCommand.COMMAND_WORD + " " + PREFIX_RELATION + "KEYWORD";
    }

    private FindCommand parseAddress(String args) throws ParseException {
        validateNotEmpty(args, buildAddressOnlyUsage());
        Matcher matcher = Pattern.compile("^\"([^\"]+)\"$").matcher(args.trim());
        if (matcher.find()) {
            String phrase = matcher.group(1);
            return new FindCommand(new AddressContainsKeywordsPredicate(List.of(phrase), true));
        }
        List<String> keywords = extractKeywords(args);
        return new FindCommand(new AddressContainsKeywordsPredicate(keywords, false));
    }

    private FindCommand parseTag(String args) throws ParseException {
        validateNotEmpty(args, buildTagOnlyUsage());
        List<String> keywords = extractKeywords(args);
        for (String keyword : keywords) {
            validateTagKeyword(keyword);
        }
        return new FindCommand(new TagContainsKeywordsPredicate(keywords));
    }

    private FindCommand parseRelation(String args) throws ParseException {
        validateNotEmpty(args, buildRelationOnlyUsage());
        String keyword = extractSingleKeyword(args, "Relation", buildRelationOnlyUsage());
        validateRelationKeyword(keyword);
        return new FindCommand(new RelationContainsKeywordPredicate(keyword));
    }

    private FindCommand parseTransaction(String args) throws ParseException {
        validateNotEmpty(args, buildTransactionOnlyUsage());
        String keyword = extractSingleKeyword(args, "Transaction stage", buildTransactionOnlyUsage());
        validateTransactionKeyword(keyword);
        return new FindCommand(new TransactionContainsKeywordPredicate(keyword));
    }

    private FindCommand parseName(String args) throws ParseException {
        validateNotEmpty(args, FindCommand.MESSAGE_USAGE);
        List<String> keywords = extractKeywords(args);
        return new FindCommand(new NameContainsKeywordsPredicate(keywords));
    }

    private String validateAndTrim(String args) throws ParseException {
        String trimmed = args.trim();
        if (trimmed.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE)
            );
        }
        if (trimmed.matches("^[a-zA-Z]+/.*")) {
            String potentialPrefix = trimmed.substring(0, trimmed.indexOf('/') + 1);
            if (!VALID_PREFIXES.contains(potentialPrefix)) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE)
                );
            }
        }
        return trimmed;
    }

    private ArgumentMultimap tokenizeArguments(String args) {
        return ArgumentTokenizer.tokenize(args, PREFIX_ADDRESS, PREFIX_TAG,
                PREFIX_RELATION, PREFIX_TRANSACTION);
    }

    private void validateNoDuplicatePrefixes(ArgumentMultimap argMultimap) throws ParseException {
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ADDRESS, PREFIX_TAG, PREFIX_RELATION, PREFIX_TRANSACTION);
    }

    private void validateOnlyOneSearchType(ArgumentMultimap argMultimap) throws ParseException {
        long prefixCount = Stream.of(PREFIX_ADDRESS, PREFIX_TAG, PREFIX_RELATION, PREFIX_TRANSACTION)
                .filter(prefix -> !argMultimap.getAllValues(prefix).isEmpty())
                .count();
        if (prefixCount > 1) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE)
            );
        }
    }

    private void validatePrefixes(ArgumentMultimap argMultimap) throws ParseException {
        validateOnlyOneSearchType(argMultimap);
        validateNoDuplicatePrefixes(argMultimap);
    }


    private void validateNotEmpty(String args, String usage) throws ParseException {
        if (args.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
        }
    }

    private void validateRelationKeyword(String keyword) throws ParseException {
        if (!VALID_RELATIONS.contains(keyword)) {
            throw new ParseException(RELATION_ERROR_MESSAGE);
        }
    }

    private void validateTransactionKeyword(String keyword) throws ParseException {
        if (!VALID_TRANSACTIONS.contains(keyword)) {
            throw new ParseException(TRANSACTION_ERROR_MESSAGE);
        }
    }

    private void validateTagKeyword(String keyword) throws ParseException {
        if (!keyword.matches("[A-Za-z0-9]+")) {
            throw new ParseException(TAG_ERROR_MESSAGE);
        }
    }

    private String extractAfterPrefix(String trimmedArgs, String prefix) {
        return trimmedArgs.substring(prefix.length()).trim();
    }

    private List<String> extractKeywords(String args) {
        return Arrays.asList(args.split("\\s+"));
    }

    private String extractSingleKeyword(String args, String fieldName, String usage) throws ParseException {
        List<String> keywords = extractKeywords(args);

        if (keywords.size() > 1) {
            throw new ParseException(
                    String.format(MESSAGE_SINGLE_KEYWORD_ONLY, fieldName, usage)
            );
        }
        return keywords.get(0).toLowerCase();
    }
}
