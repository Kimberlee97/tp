package homey.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import homey.commons.core.index.Index;
import homey.commons.util.StringUtil;
import homey.logic.parser.exceptions.ParseException;
import homey.model.person.Address;
import homey.model.person.Email;
import homey.model.person.Meeting;
import homey.model.person.Name;
import homey.model.person.Phone;
import homey.model.person.Remark;
import homey.model.tag.Relation;
import homey.model.tag.Tag;
import homey.model.tag.TransactionStage;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Invalid index: index must be a positive integer "
            + "(starting from 1) and within the displayed list range.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses a {@code String stage} into a {@code TransactionStage}.
     *
     * @throws ParseException if the given {@code} stage is invalid.
     */
    public static TransactionStage parseStage(String stage) throws ParseException {
        requireNonNull(stage);
        String trimmed = stage.trim();
        if (trimmed.isEmpty() || !TransactionStage.isValid(trimmed)) {
            throw new ParseException(TransactionStage.MESSAGE_CONSTRAINTS);
        }
        return new TransactionStage(trimmed);
    }

    /**
     * Parses a {@code String relation} into a {@code Relation}.
     *
     * @throws ParseException if the given {@code} relation is invalid.
     */
    public static Relation parseRelation(String relation) throws ParseException {
        requireNonNull(relation);
        String trimmed = relation.trim().toLowerCase();
        if (trimmed.isEmpty() || !Relation.isValidRelation(trimmed)) {
            throw new ParseException(Relation.MESSAGE_CONSTRAINTS);
        }
        return new Relation(trimmed);
    }

    /**
     * Parses a {@code String remark} into a {@code Remark}.
     *
     * @throws ParseException if the given {@code} remark is invalid.
     */
    public static Remark parseRemark(String remark) throws ParseException {
        requireNonNull(remark);
        String trimmed = remark.trim();
        try {
            return new Remark(trimmed);
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage());
        }
    }

    /**
     * Converts a string into a {@code Meeting} object.
     * Trims whitespace and checks that the format is valid.
     *
     * @param value Meeting date and time string to parse.
     * @return A {@code Meeting} object with the given date and time.
     * @throws ParseException If the string format is invalid.
     */
    public static Meeting parseMeeting(String value) throws ParseException {
        String trimmed = value.trim();
        if (!Meeting.isValidMeeting(trimmed)) {
            throw new ParseException(Meeting.MESSAGE_CONSTRAINTS);
        }
        return new Meeting(trimmed);
    }

}
