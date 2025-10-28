package homey.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.function.Predicate;

import homey.model.Model;
import homey.model.person.Meeting;
import homey.model.person.Person;

/**
 * Lists all persons that have a meeting, sorted by the earliest meeting first.
 */
public class ListMeetingCommand extends Command {
    public static final String MESSAGE_SUCCESS = "Listed contacts with meetings (earliest first).";
    public static final String MESSAGE_EMPTY = "No contacts with meetings found.";

    // Only persons that have a meeting AND are not archived
    private static final Predicate<Person> HAS_MEETING_ACTIVE =
            p -> p.getMeeting() != null && p.getMeeting().isPresent() && !p.isArchived();

    // Sort by meeting time ascending; if anything is missing, push to the end.
    // If times are equal, break ties by name (case-insensitive A→Z).
    private static final Comparator<Person> BY_MEETING_ASC = Comparator
            .comparing((Person p) -> p.getMeeting()
                    .map(Meeting::getDateTime)
                    .orElse(LocalDateTime.MAX))
            .thenComparing(p -> p.getName().fullName, String.CASE_INSENSITIVE_ORDER);

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(HAS_MEETING_ACTIVE);
        model.sortFilteredPersonListBy(BY_MEETING_ASC);
        if (model.getFilteredPersonList().isEmpty()) {
            return new CommandResult(MESSAGE_EMPTY);
        }
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ListMeetingCommand;
    }
}
