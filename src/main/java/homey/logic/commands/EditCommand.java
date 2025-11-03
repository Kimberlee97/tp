package homey.logic.commands;

import static homey.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static homey.logic.parser.CliSyntax.PREFIX_EMAIL;
import static homey.logic.parser.CliSyntax.PREFIX_MEETING;
import static homey.logic.parser.CliSyntax.PREFIX_NAME;
import static homey.logic.parser.CliSyntax.PREFIX_PHONE;
import static homey.logic.parser.CliSyntax.PREFIX_RELATION;
import static homey.logic.parser.CliSyntax.PREFIX_REMARK;
import static homey.logic.parser.CliSyntax.PREFIX_TAG;
import static homey.logic.parser.CliSyntax.PREFIX_TRANSACTION;
import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import homey.commons.core.index.Index;
import homey.commons.util.CollectionUtil;
import homey.commons.util.ToStringBuilder;
import homey.logic.Messages;
import homey.logic.commands.exceptions.CommandException;
import homey.model.Model;
import homey.model.person.Address;
import homey.model.person.Email;
import homey.model.person.Meeting;
import homey.model.person.Name;
import homey.model.person.Person;
import homey.model.person.Phone;
import homey.model.person.Remark;
import homey.model.tag.Relation;
import homey.model.tag.Tag;
import homey.model.tag.TransactionStage;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_RELATION + "RELATION] "
            + "[" + PREFIX_TRANSACTION + "TRANSACTION STAGE] "
            + "[" + PREFIX_REMARK + "REMARK]"
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "[" + PREFIX_MEETING + "MEETING_DATETIME]\n"
            + "Tip: use " + PREFIX_MEETING + " to clear the meeting (e.g., 'm/').\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com "
            + PREFIX_MEETING + "2025-11-10 09:30";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";
    public static final String MESSAGE_EDIT_MEETING_SET = "Updated meeting for %1$s: %2$s";
    public static final String MESSAGE_EDIT_MEETING_CLEARED = "Cleared meeting for %1$s.";
    public static final String MESSAGE_EDIT_MEETING_NONE = "No meetings to clear for %1$s.";
    public static final String MESSAGE_USAGE_MEETING_ONLY = COMMAND_WORD + " INDEX m/DATE_TIME\n"
                    + "or: " + COMMAND_WORD + " INDEX m/ (to clear meeting)\n"
                    + "Note: When editing a meeting, no other fields may be provided.";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    private String composeFeedback(Person before, Person after, EditPersonDescriptor d) {
        StringBuilder fb = new StringBuilder();

        if (d.hasNonMeetingEdits()) {
            fb.append(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(after)));
        }

        if (d.isMeetingEdited()) {
            if (fb.length() > 0) {
                fb.append(System.lineSeparator());
            }
            final String name = after.getName().toString();
            if (d.getMeeting().isPresent()) {
                String when = after.getMeeting().map(Meeting::toDisplayString).orElse("<unknown>");
                fb.append(String.format(MESSAGE_EDIT_MEETING_SET, name, when));
            } else {
                fb.append(before.getMeeting().isEmpty()
                        ? String.format(MESSAGE_EDIT_MEETING_NONE, name)
                        : String.format(MESSAGE_EDIT_MEETING_CLEARED, name));
            }
        }

        return (fb.length() == 0)
                ? String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(after))
                : fb.toString();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        if (personToEdit.isArchived()) {
            throw new CommandException(Messages.MESSAGE_CANNOT_EDIT_ARCHIVED);
        }
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);

        String feedback = composeFeedback(personToEdit, editedPerson, editPersonDescriptor);
        return new CommandResult(feedback);
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        TransactionStage updatedStage = editPersonDescriptor.getStage().orElse(personToEdit.getStage());
        Relation updatedRelation = editPersonDescriptor.getRelation().orElse(personToEdit.getRelation());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());
        Remark updatedRemark = editPersonDescriptor.getRemark().orElse(personToEdit.getRemark());

        Optional<Meeting> updatedMeeting = editPersonDescriptor.isMeetingEdited()
                ? editPersonDescriptor.getMeeting()
                : personToEdit.getMeeting();

        Person edited = new Person(updatedName, updatedPhone, updatedEmail, updatedAddress,
                updatedRelation, updatedStage, updatedRemark, updatedTags, updatedMeeting);
        if (personToEdit.isArchived()) {
            edited = edited.archived();
        }
        return edited;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Relation relation;
        private TransactionStage stage;
        private Remark remark;
        private Set<Tag> tags;

        private boolean meetingEdited = false;
        private Optional<Meeting> meeting = Optional.empty();

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setRelation(toCopy.relation);
            setStage(toCopy.stage);
            setRemark(toCopy.remark);
            setTags(toCopy.tags);
            this.meetingEdited = toCopy.meetingEdited;
            this.meeting = toCopy.meeting;
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, relation, stage, remark, tags)
                    || meetingEdited; // include meeting edits
        }

        /** True if any non-meeting field is edited. */
        public boolean hasNonMeetingEdits() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, relation, stage, remark, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setRelation(Relation relation) {
            this.relation = relation;
        }

        public Optional<Relation> getRelation() {
            return Optional.ofNullable(relation);
        }

        public void setStage(TransactionStage stage) {
            this.stage = stage;
        }

        public Optional<TransactionStage> getStage() {
            return Optional.ofNullable(stage);
        }

        public void setRemark(Remark remark) {
            this.remark = remark;
        }

        public Optional<Remark> getRemark() {
            return Optional.ofNullable(remark);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        /**
         * Marks the meeting as edited and sets the desired value.
         * Pass {@code Optional.empty()} to clear the meeting.
         */
        public void setMeeting(Optional<Meeting> meeting) {
            this.meetingEdited = true;
            this.meeting = (meeting == null) ? Optional.empty() : meeting;
        }

        /** Returns the desired meeting value if edited (may be empty to indicate clearing). */
        public Optional<Meeting> getMeeting() {
            return meeting;
        }

        /** Returns true if the user supplied an {@code m/} prefix (edit or clear). */
        public boolean isMeetingEdited() {
            return meetingEdited;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            EditPersonDescriptor otherEditPersonDescriptor = (EditPersonDescriptor) other;
            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && Objects.equals(relation, otherEditPersonDescriptor.relation)
                    && Objects.equals(stage, otherEditPersonDescriptor.stage)
                    && Objects.equals(remark, otherEditPersonDescriptor.remark)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags)
                    && meetingEdited == otherEditPersonDescriptor.meetingEdited
                    && Objects.equals(meeting, otherEditPersonDescriptor.meeting);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("relation", relation)
                    .add("transaction stage", stage)
                    .add("remark", remark)
                    .add("tags", tags)
                    .add("meeting", meeting)
                    .toString();
        }
    }
}
