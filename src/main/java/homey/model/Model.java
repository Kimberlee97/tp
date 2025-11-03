package homey.model;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;

import homey.commons.core.GuiSettings;
import homey.model.person.Person;
import javafx.collections.ObservableList;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /** {@code Predicate} that shows only non-archived persons. */
    Predicate<Person> PREDICATE_SHOW_ACTIVE_PERSONS = p -> !p.isArchived();

    /** {@code Predicate} that shows only archived persons. */
    Predicate<Person> PREDICATE_SHOW_ARCHIVED_PERSONS = Person::isArchived;

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns the user prefs' address book file path.
     */
    Path getAddressBookFilePath();

    /**
     * Sets the user prefs' address book file path.
     */
    void setAddressBookFilePath(Path addressBookFilePath);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /**
     * Sets the selected person. UI listens to this property to show the contact card.
     */
    void setSelectedPerson(Person person);

    Optional<Person> getSelectedPerson();

    /** Updates the overdue status of all meetings in the address book */
    void updateMeetingOverdueStatus();

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Sorts the currently displayed (filtered) list of persons using the given comparator.
     * <p>
     * The comparator defines the ordering of persons in the list, for example by name or meeting date.
     * The sort is applied on top of the existing filtered view, and does not modify the underlying data.
     *
     * @param comparator Comparator used to determine the order of persons in the filtered list.
     */
    void sortFilteredPersonListBy(java.util.Comparator<homey.model.person.Person> comparator);

    /**
     * Clears any applied sorting on the person list, restoring the natural or insertion order.
     * <p>
     * This method is typically called when the user issues a {@code list} command to reset the view
     * back to its default order after sorting has been applied (for example, after {@code list meeting}).
     */
    void clearPersonListSorting();

}
