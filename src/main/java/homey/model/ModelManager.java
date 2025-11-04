package homey.model;

import static homey.commons.util.CollectionUtil.requireAllNonNull;
import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import homey.commons.core.GuiSettings;
import homey.commons.core.LogsCenter;
import homey.model.person.Meeting;
import homey.model.person.Person;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final SortedList<Person> sortedPersons;
    private final ObjectProperty<Person> selectedPerson = new SimpleObjectProperty<>(null);

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     * By default, the filtered list shows only active (non archived) persons.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);

        this.filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS);

        this.sortedPersons = new SortedList<>(this.filteredPersons);

    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);
        addressBook.setPerson(target, editedPerson);
    }

    @Override
    public void updateMeetingOverdueStatus() {
        addressBook.getPersonList().forEach(person ->
                person.getMeeting().ifPresent(Meeting::updateOverdueStatus));
    }

    //=========== Filtered / Sorted Person List Accessors ====================================================

    /** Returns the *sorted* view (which wraps the filtered list). */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return sortedPersons; // return the SortedList view
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public void sortFilteredPersonListBy(Comparator<Person> comparator) {
        sortedPersons.setComparator(comparator);
    }

    @Override
    public void clearPersonListSorting() {
        sortedPersons.setComparator(null); // back to source order
    }

    @Override
    public void setSelectedPerson(Person person) {
        selectedPerson.set(person);
    }

    @Override
    public Optional<Person> getSelectedPerson() {
        return Optional.ofNullable(selectedPerson.get());
    }

    public ObjectProperty<Person> selectedPersonProperty() {
        return selectedPerson;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ModelManager)) {
            return false;
        }
        ModelManager o = (ModelManager) other;
        // equality on lists: compare the underlying filtered list (order-insensitive equality isn’t needed here)
        return addressBook.equals(o.addressBook)
                && userPrefs.equals(o.userPrefs)
                && filteredPersons.equals(o.filteredPersons);
    }
}
