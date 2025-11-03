package homey.logic.commands;

import static homey.testutil.Assert.assertThrows;
import static homey.testutil.TypicalPersons.ALICE;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import homey.commons.core.GuiSettings;
import homey.logic.Messages;
import homey.logic.commands.exceptions.CommandException;
import homey.model.AddressBook;
import homey.model.Model;
import homey.model.ReadOnlyAddressBook;
import homey.model.ReadOnlyUserPrefs;
import homey.model.person.Person;
import homey.testutil.PersonBuilder;
import javafx.collections.ObservableList;

public class AddCommandTest {

    @Test
    public void constructor_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null));
    }

    @Test
    public void execute_personAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Person validPerson = new PersonBuilder().build();

        CommandResult commandResult = new AddCommand(validPerson).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                commandResult.getFeedbackToUser());
        assertEquals(Arrays.asList(validPerson), modelStub.personsAdded);
    }

    @Test
    public void execute_personWithMeeting_addsSuccessfullyAndShowsMeetingMessage() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Person personWithMeeting = new PersonBuilder().withMeeting("2025-11-03 14:00").build();

        CommandResult result = new AddCommand(personWithMeeting).execute(modelStub);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("New person added"));
        assertTrue(feedback.contains("Next meeting"));
        assertTrue(feedback.contains("2025-11-03 14:00"));
        assertEquals(Arrays.asList(personWithMeeting), modelStub.personsAdded);
    }

    @Test
    public void execute_personAlreadyArchived_throwsCommandException() {
        Person archivedPerson = new PersonBuilder().withName("Alice Tan").build().archived();
        AddCommand addCommand = new AddCommand(new PersonBuilder(archivedPerson).build());
        Model modelStub = new ModelStubWithArchivedDuplicate(archivedPerson);

        assertThrows(CommandException.class,
                Messages.MESSAGE_DUPLICATE_IN_ARCHIVED, () -> addCommand.execute(modelStub));
    }

    @Test
    public void execute_duplicateInArchived_throwsCommandException() {
        // archived version of an existing person
        Person archivedPerson = new PersonBuilder().withName("Alice Tan").build().archived();
        Person samePerson = new PersonBuilder(archivedPerson).build(); // identical fields

        // ModelStub that simulates an archived duplicate
        Model modelStub = new ModelStubWithArchivedDuplicate(archivedPerson);

        AddCommand addCommand = new AddCommand(samePerson);

        // Expect CommandException for duplicate in archived
        assertThrows(CommandException.class,
                Messages.MESSAGE_DUPLICATE_IN_ARCHIVED, () -> addCommand.execute(modelStub));
    }

    @Test
    public void execute_duplicateInArchivedInteractiveMode_addsInteractiveHint() {
        // archived duplicate already present
        Person archived = new PersonBuilder().withName("Alice Tan").build().archived();
        // same person (not archived) being added
        Person toAdd = new PersonBuilder(archived).build();

        Model modelStub = new ModelStubWithArchivedDuplicate(archived);

        // interactive = true, and no missing fields -> we enter the same block,
        // but message should include the interactive hint.
        AddCommand interactiveCmd = new AddCommand(toAdd, true, new HashMap<>());

        assertThrows(CommandException.class,
                Messages.MESSAGE_DUPLICATE_IN_ARCHIVED + "\n"
                        + InteractiveCommand.MESSAGE_INTERACTIVE, () -> interactiveCmd.execute(modelStub));
    }


    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person validPerson = new PersonBuilder().build();
        AddCommand addCommand = new AddCommand(validPerson);
        ModelStub modelStub = new ModelStubWithPerson(validPerson);

        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_PERSON, () -> addCommand.execute(modelStub));
    }

    @Test
    public void equals() {
        Person alice = new PersonBuilder().withName("Alice").build();
        Person bob = new PersonBuilder().withName("Bob").build();
        AddCommand addAliceCommand = new AddCommand(alice);
        AddCommand addBobCommand = new AddCommand(bob);

        // same object -> returns true
        assertTrue(addAliceCommand.equals(addAliceCommand));

        // same values -> returns true
        AddCommand addAliceCommandCopy = new AddCommand(alice);
        assertTrue(addAliceCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        assertFalse(addAliceCommand.equals(1));

        // null -> returns false
        assertFalse(addAliceCommand.equals(null));

        // different person -> returns false
        assertFalse(addAliceCommand.equals(addBobCommand));
    }

    @Test
    public void toStringMethod() {
        AddCommand addCommand = new AddCommand(ALICE);
        String expected = AddCommand.class.getCanonicalName() + "{toAdd=" + ALICE + "}";
        assertEquals(expected, addCommand.toString());
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateMeetingOverdueStatus() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void sortFilteredPersonListBy(Comparator<Person> comparator) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void clearPersonListSorting() {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single person.
     */
    private class ModelStubWithPerson extends ModelStub {
        private final Person person;

        ModelStubWithPerson(Person person) {
            requireNonNull(person);
            this.person = person;
        }

        @Override
        public boolean hasPerson(Person person) {
            requireNonNull(person);
            return this.person.isSamePerson(person);
        }

        @Override
        public void updateMeetingOverdueStatus() {
            if (this.person.getMeeting().isPresent()) {
                this.person.getMeeting().get().updateOverdueStatus();
            }
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook(); // empty is fine for this test
        }

    }

    /**
     * A Model stub that always accept the person being added.
     */
    private class ModelStubAcceptingPersonAdded extends ModelStub {
        final ArrayList<Person> personsAdded = new ArrayList<>();

        @Override
        public boolean hasPerson(Person person) {
            requireNonNull(person);
            return personsAdded.stream().anyMatch(person::isSamePerson);
        }

        @Override
        public void addPerson(Person person) {
            requireNonNull(person);
            personsAdded.add(person);
        }

        @Override
        public void updateMeetingOverdueStatus() {
            personsAdded.forEach(person -> {
                if (person.getMeeting().isPresent()) {
                    person.getMeeting().get().updateOverdueStatus();
                }
            });
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }

    private class ModelStubWithArchivedDuplicate extends ModelStub {
        private final Person archived;

        ModelStubWithArchivedDuplicate(Person archived) {
            this.archived = archived;
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            AddressBook ab = new AddressBook();
            ab.addPerson(archived);
            return ab;
        }

        @Override
        public boolean hasPerson(Person p) {
            return false;
        } // not in active list
        @Override
        public void addPerson(Person p) {
            throw new AssertionError("Should not add");
        }
    }
}
