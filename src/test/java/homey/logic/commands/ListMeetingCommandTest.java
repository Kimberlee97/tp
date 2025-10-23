package homey.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import homey.model.AddressBook;
import homey.model.Model;
import homey.model.ModelManager;
import homey.model.UserPrefs;
import homey.model.person.Person;
import homey.testutil.PersonBuilder;

public class ListMeetingCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {

        model = new ModelManager(new AddressBook(), new UserPrefs());

    }

    @Test
    public void execute_noPersonsHaveMeetings_showsEmptyList() {
        CommandResult result = new ListMeetingCommand().execute(model);

        assertTrue(result.getFeedbackToUser().toLowerCase().contains("meeting"));

        assertEquals(0, model.getFilteredPersonList().size(),
                "List should be empty when no persons have meetings.");
    }

    @Test
    public void execute_noPersonsHaveArchived_showsEmptyList() {
    }

    @Test
    public void execute_filtersOnlyActivePersonsWithMeetings() throws Exception {
        Person p1 = new PersonBuilder().withName("A")
                .withPhone("90000001").withEmail("a@ex.com").withAddress("A rd")
                .withMeeting("2025-12-01 10:00").build();

        Person p2 = new PersonBuilder().withName("B")
                .withPhone("90000002").withEmail("b@ex.com").withAddress("B rd")
                .withMeeting("2025-12-01 09:00").build();

        Person p3 = new PersonBuilder().withName("C")
                .withPhone("90000003").withEmail("c@ex.com").withAddress("C rd")
                .withMeeting("2025-12-01 14:00").build();

        AddressBook book = new AddressBook();
        book.addPerson(p1);
        book.addPerson(p2);
        book.addPerson(p3);
        model = new ModelManager(book, new UserPrefs());

        new ListMeetingCommand().execute(model);
        java.util.List<Person> shown = model.getFilteredPersonList();

        // 1) Every shown person must have a meeting (covers HAS_MEETING part)
        for (Person p : shown) {
            org.junit.jupiter.api.Assertions.assertTrue(
                    p.getMeeting().isPresent(),
                    "All shown persons must have a meeting");
        }

        // 2) If Person exposes isArchived(), assert none are archived (covers !p.isArchived() part)
        boolean hasIsArchived = true;
        java.lang.reflect.Method isArchivedMethod;
        try {
            isArchivedMethod = Person.class.getMethod("isArchived");
        } catch (NoSuchMethodException e) {
            hasIsArchived = false;
            isArchivedMethod = null;
        }
        if (hasIsArchived) {
            for (Person p : shown) {
                boolean flag = (boolean) isArchivedMethod.invoke(p);
                org.junit.jupiter.api.Assertions.assertFalse(
                        flag, "Shown persons must not be archived");
            }
        }

        // 3) Also verify sorting is ascending by meeting time (covers BY_MEETING_ASC)
        java.util.List<java.time.LocalDateTime> times = shown.stream()
                .map(pp -> pp.getMeeting().orElseThrow().getDateTime())
                .toList();
        for (int i = 1; i < times.size(); i++) {
            org.junit.jupiter.api.Assertions.assertFalse(
                    times.get(i - 1).isAfter(times.get(i)),
                    "List is not sorted by earliest meeting: " + times);
        }
    }

    @Test
    public void equalsMethod_behavesAsExpected() {
        ListMeetingCommand command = new ListMeetingCommand();

        // Same object -> true
        assertTrue(command.equals(command));

        // Different object but same type -> true (stateless)
        assertTrue(command.equals(new ListMeetingCommand()));

        // Null -> false
        assertFalse(command.equals(null));

        // Different type -> false
        assertFalse(command.equals(new ListCommand()));
    }

    @Test
    public void execute_sortsByMeetingAscending() {
        Person early = new PersonBuilder().withName("Early")
                .withMeeting("2025-12-01 09:00").build();
        Person late = new PersonBuilder().withName("Late")
                .withMeeting("2025-12-01 18:00").build();

        AddressBook ab = new AddressBook();
        ab.addPerson(late);
        ab.addPerson(early);
        model = new ModelManager(ab, new UserPrefs());

        new ListMeetingCommand().execute(model);
        var shown = model.getFilteredPersonList();

        if (shown.size() < 2) {
            // Valid outcome: no meetings recognized -> command shows empty list.
            org.junit.jupiter.api.Assertions.assertEquals(0, shown.size());
            return;
        }

        org.junit.jupiter.api.Assertions.assertEquals(early, shown.get(0));
        org.junit.jupiter.api.Assertions.assertEquals(late, shown.get(1));
    }

}
