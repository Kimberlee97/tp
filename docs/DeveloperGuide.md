---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# AB-3 Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `HelpWindow` in the UI component was enhanced to open the User Guide directly in the user’s browser.  
If the browser cannot be opened (e.g., in headless environments), the fallback "Help" window remains available to copy the link manually.  
Additionally, the "Help" command can now open the specific section of the User Guide based on the topic provided (e.g., `help add`, `help edit`).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

Additional parsers added for this feature:
* `ArchiveCommandParser` – parses `archive INDEX`
* `UnarchiveCommandParser` – parses `unarchive INDEX`
* `ListCommandParser` – parses `list` (active) and `list archive` (archived)

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

#### Archive state & filtered lists

To support hiding archived contacts from the default view, `Person` has an immutable boolean
field `archived`. Two predicates are exposed via `Model`:
* `PREDICATE_SHOW_ACTIVE_PERSONS` – returns persons where `!person.isArchived()`
* `PREDICATE_SHOW_ARCHIVED_PERSONS` – returns persons where `person.isArchived()`

`ModelManager` sets the current filter to **active** by default and switches filters in
`ArchiveCommand`, `UnarchiveCommand`, and `ListCommand` (see Implementation).

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

#### Persisting archive state

`JsonAdaptedPerson` reads/writes an `archived` boolean. Older save files without this field
are still accepted; the value defaults to `false` (active) on load for backward compatibility.

### Common classes

Classes used by multiple components are in the `homey.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving / unarchiving

#### Goal
Allow users to hide completed/irrelevant contacts from the default list while keeping them retrievable.

#### Model changes
* `Person` is extended with an immutable boolean `archived`.
  * Constructors accept `archived`.
  * Query helpers: `isArchived()`, `archived()` (returns a copy with `archived=true`), and
      `unarchived()` (copy with `archived=false`).
* `Model` exposes
  * `PREDICATE_SHOW_ACTIVE_PERSONS` and `PREDICATE_SHOW_ARCHIVED_PERSONS`.
  * `ModelManager` sets the active predicate by default.

#### Storage changes
* `JsonAdaptedPerson` includes an `archived` field.
  * Missing field on load defaults to `false` for backward compatibility.

#### Logic changes
* **Archive** – `archive INDEX`
  * Validates index against the current filtered list.
  * If already archived, throws `CommandException("This person is already archived.")`.
  * Replaces the target with `person.archived()` via `model.setPerson(...)`.
  * Keeps the user in the **active** view by calling
    `model.updateFilteredPersonList(Model.PREDICATE_SHOW_ACTIVE_PERSONS)`.

**UML Diagrams**
  <puml src="diagrams/archive/ArchiveClass.puml" width="420" />
  <puml src="diagrams/archive/ArchiveSequence.puml" width="620" />
  <puml src="diagrams/archive/ParserClass.puml" width="420" />

* **Unarchive** – `unarchive INDEX`
  * Intended for use from the archived view.
  * Validates index; if not archived, throws `CommandException("This person is not archived.")`.
  * Replaces with `person.unarchived()` and switches the filter back to
    `PREDICATE_SHOW_ACTIVE_PERSONS` so the contact reappears in the main list.
* **List** – `list` / `list archive`
  * Implemented via `ListCommandParser`.
  * `list` (or `list active`) sets `PREDICATE_SHOW_ACTIVE_PERSONS`.
  * `list archive` sets `PREDICATE_SHOW_ARCHIVED_PERSONS`.

**UML Diagrams**
  <puml src="diagrams/unarchive/UnarchiveClass.puml" width="420" />
  <puml src="diagrams/unarchive/UnarchiveSequence.puml" width="620" />
  <puml src="diagrams/unarchive/ParserClass.puml" width="420" />

#### Error handling (user-visible)
* Invalid index → `Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX`.
* Archiving an already archived contact → “This person is already archived.”
* Unarchiving an active contact → “This person is not archived.”

#### Rationale & alternatives
* **Why a boolean on `Person`?** Simple, stable, and cheap to persist/filter.
* **Why predicates not a field on `Model` state?** Keeps UI binding unchanged; only the predicate changes.

### [Proposed] List contacts by meeting date and time

#### Proposed Implementation

The proposed `list meeting` command enhances the `Logic` component by allowing users to view all contacts that have an upcoming meeting scheduled, ordered from the earliest to latest meeting date.

This feature is implemented through the new `ListMeetingCommand` class, which extends the existing `Command` abstraction and works seamlessly with the existing `ListCommandParser`.

The parsing and execution flow are as follows:
* The user enters `list meeting` in the command box.
* `AddressBookParser` detects that the command word is `list` and delegates to `ListCommandParser`.
* `ListCommandParser` interprets the argument `meeting` and returns a new `ListMeetingCommand`.
* Upon execution, `ListMeetingCommand` filters the person list to include only those with a meeting scheduled (i.e., `person.getMeeting().isPresent()`), excluding archived persons.
* The filtered list is then sorted by meeting date and time in ascending order, with names used as a secondary tiebreaker.

This ensures that the user can easily see which meetings are coming up first, providing a chronological overview of scheduled client engagements.

The following class diagram illustrates how `ListMeetingCommand` integrates with the existing `Logic` component:

<puml src="diagrams/ListMeetingClassDiagram.puml" alt="ListMeetingClassDiagram" />

#### Example Usage

Given below is an example usage scenario and how the feature behaves at each step.

Step&nbsp;1. The user launches the application and types the command `list meeting`.

Step&nbsp;2. `LogicManager` calls `AddressBookParser#parseCommand("list meeting")`.  
The `AddressBookParser` passes the argument `"meeting"` to `ListCommandParser`.

Step&nbsp;3. `ListCommandParser` recognizes the argument and returns a new instance of `ListMeetingCommand`.

Step&nbsp;4. `LogicManager` executes the command, which calls `model.updateFilteredPersonList(predicate)` and `model.sortFilteredPersonListBy(comparator)` to filter and order the contacts by meeting time.

Step&nbsp;5. The UI (`PersonListPanel`) automatically refreshes to display only the persons with meetings, sorted from earliest to latest.

The following sequence diagram shows how the `list meeting` command flows through the `Logic` component:

<puml src="diagrams/ListMeetingSequenceDiagram-Logic.puml" alt="ListMeetingSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** Persons without a meeting or that have been archived are excluded from the filtered list.  
The comparator used ensures ascending order by meeting date and time; if two meetings share the same time, contacts are ordered alphabetically by name.

</box>

#### Model Interaction

The `ModelManager` exposes a sorted view layered on top of the filtered view:

* `updateFilteredPersonList(predicate)` narrows the list to relevant persons (for `list meeting`: persons with a meeting and not archived).
* `sortFilteredPersonListBy(comparator)` sets a comparator on the `SortedList` that wraps the filtered list; it does not mutate data.
* `clearPersonListSorting()` removes any comparator, restoring the original order.
* `getFilteredPersonList()` returns the `SortedList<Person>` that the UI binds to.

<puml src="diagrams/ListMeetingModelDiagram.puml" alt="ListMeetingModelDiagram" />

#### Error handling (user-visible)

* Invalid `list` variant (e.g., `list mm`) →  
  `Invalid command format!` followed by
  list: Lists persons.
  Usage: list [archive | meeting]
  Examples: list | list archive | list meeting
* No contacts have meetings → command succeeds with normal success message; the list simply shows **0** persons.
* Internal failures (e.g., null model) are guarded via `requireNonNull` and handled as assertions during development; no user-visible state change occurs.

#### Rationale & alternatives

* **Why a separate `ListMeetingCommand`?**  
  Keeps the `list` family modular (mirrors `list archive`). Clear separation of concerns, easier testing, consistent with AB3’s command-per-variant pattern.

* **Why predicates rather than toggling model state?**  
  The UI already binds to an observable list. Changing only the **predicate** (and comparator) avoids additional state flags and minimises UI coupling.

* **Why the two-level ordering (date, then name)?**  
  Predictable and stable ordering in the presence of ties: first by earliest meeting, then case-insensitive alphabetical by name.

* **Alternative considered – add a `meeting` flag to `Person`**  
  Rejected. Meeting is already a dedicated value object; filtering by `Optional<Meeting>` is simpler and avoids redundancy.

* **Alternative considered – merge into `ListCommand`**  
  Rejected to prevent command bloat and branching complexity in a single class. A dedicated command keeps behaviour explicit and testable.

#### Design considerations

* **Simplicity**: Uses a `Predicate<Person>` + `Comparator<Person>` only; no new model fields or persistence changes.
* **Safety**: Nulls are guarded with `requireNonNull`. Missing meetings are treated as `LocalDateTime.MAX` so they sort to the end, but such persons are filtered out anyway.
* **Extensibility**: Future variants (e.g., `list by tag`, `list upcoming <N> days`) can reuse the same pattern with different predicates/comparators.

#### Design Considerations

**Aspect:** How to incorporate meeting-based listing within the existing list framework
* **Alternative 1 (Chosen):** Introduce a dedicated `ListMeetingCommand` that cleanly extends `Command`
    * Pros: Adheres to the Single Responsibility Principle; keeps `list` behavior modular and consistent with existing `list archive` logic.
    * Cons: Adds one additional command class.

* **Alternative 2:** Merge functionality into the existing `ListCommand`
    * Pros: Fewer classes.
    * Cons: Blurs responsibility between generic listing and meeting-specific logic; complicates parser branching and testing.

---

**Rationale:**  
Alternative 1 was chosen as it offers clear separation of concerns, testability, and extensibility (future list variants like `list active`, `list by tag` can reuse the same parser pattern).


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**: Property Agent

* has a need to manage a significant number of contacts
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: 

**What problem does the product solve?**
Difficult to organise contacts and tasks on a singular centralised platform
Easy to forget meetings when they’re tracked on separate apps
Hard to navigate
Grouping stakeholders by location/transaction

**How does it make the user's life easier?**
Centralised platform for property agents to use to track, sort and schedule contacts in order for them to work more efficiently and stay organised
Calendar reminders to keep the user on track with their schedule
Able to be used on the go

**What is the boundary beyond which the app will not help?**
Unable to track legal/financial processing
Not CRM replacement
No marketing/listing management

**Persona:**
Working adult, graduate already
popular so a lot of clients
generally independent but collaborates with other agents
On the go since need to travel a lot to different property, so need something convenient
Prefers typing to mouse usage
Slightly forgetful due to large number of clients, hard to track

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a ...                                               | I want to ...                                                          | So that I can ...                                                                |
|----------|--------------------------------------------------------|------------------------------------------------------------------------|----------------------------------------------------------------------------------|
| `* *`    | Property Agent                                         | view my contacts and schedule in one platform                          | I don’t have to switch between apps                                              |
| `* *`    | Property Agent with many clients and colleagues        | sort my contacts into categories (tagging)                             | it is easier to remember my relationship with them                               |
| `* *`    | Forgetful user                                         | Get reminded of meetings and appointments when they are approaching    | I don’t ghost my clients or agents                                               |
| `*`      | Busy property agent                                    | view my schedule and contact list on-the-go                            | I can easily access necessary information when I’m outside or in a rush          |
| `* *`    | Property Agent has to juggle multiple stakeholders     | Set reminders linked to specific contacts                              | I don’t miss client meetings or property showings                                |
| `*`      | Property agent                                         | Add notes visible only to me                                           | Keep private thoughts while still sharing client basics                          |
| `* *`    | Property Agent managing clients                        | Sort clients and agents by type                                        | I can filter out which types of deals I want to focus on at any point of time    |
| `*`      | Forgetful User                                         | Add detail related to the events in my schedule e.g. event description | I can remember what to prepare                                                   |
| `*`      | Property agent                                         | Attach notes to each contact                                           | I can remember important details                                                 |
| `*`      | Property agent that travels a lot                      | Group my clients and deals by area                                     | I can easily plan out meetings by proximity                                      |
| `* *`    | Property agent                                         | Set recurring reminders like weekly check-in calls                     | I can maintain consistent follow-ups                                             |
| `* *`    | Property Agent                                         | Link a meeting to multiple contacts                                    | Group viewings or negotiations are scheduled smoothly                            |
| `* *`    | Property agent                                         | Tag contacts by transaction stage like prospect, negotiating, closed   | I can track progress easily                                                      |
| `* *`    | User that prefers visual information                   | Colour code my tags and events                                         | I can easily identify the type of contacts and events                            |
| `* *`    | Property Agent                                         | Set overdue tasks or meetings highlighted                              | I can prioritise catching up quickly                                             |
| `* *`    | Property agent                                         | Add dates for meetings                                                 | I can keep track of important events and attend them                             |
| `* *`    | Property agent that wants to track their current deals | Sort contacts by earliest meeting                                      | I can prioritise deals or meetings that have been delayed or are taking too long |
| `* *`    | Forgetful property agent                               | Search for contacts using partial names                                | I can find their contact information despite not remembering their full name     |
| `*`      | Property agent                                         | Input details for a new contact in one line                            | It is convenient                                                                 |
| `* *`    | Property agent who handles many meetings               | Tag each client with their property location                           | I can quickly group and search for clients by area                               |
| `* * *`  | Property agent who prefers typing                      | Search for client by typing their name                                 | Save time instead of scrolling through the whole address book                    |
| `* *`    | Property agent managing multiple tasks                 | View upcoming meeting with the nearest deadline first                  | I know which client to attend to next                                            |
| `*`      | Property agent who prefers typing                      | Autocomplete names/commands as I search through the addressbook        | Find my client more efficiently                                                  |
| `*`      | Property agent juggling many deals                     | Attach notes to each client’s profile                                  | I can remember key details of past conversations                                 |
| `*`      | Property agent                                         | See which agent is linked to a shared client                           | Responsibilities are clear                                                       |
| `* *`    | Property agent that wants to track deal history        | Sort contacts by date added as contact                                 | Prioritise loyal customers                                                       |
| `*`      | Property agent                                         | Write multiple different commands to do the same thing                 | I don’t have to remember specific syntax for each command                        |
| `*`      | Property agent with a lot of contacts                  | Archive contacts that have completed deals                             | I can prioritise contacts that I have ongoing deals with                         |
| `*`      | Forgetful user                                         | Write simpler commands intuitively and when prompted to                | I don’t have to remember the complex syntax for each command                     |
| `* *`    | Property agent                                         | Sort contacts by alphabetical order                                    | Easier to locate contacts within address book                                    |
| `* *`    | Property agent                                         | Edit contacts to add new information about them                        | I don’t have to delete and add contacts to add more information                  |
| `* * *`  | Property agent                                         | Delete contacts that I no longer require                               | I have a less cluttered contact list that is easier to navigate                  |
| `* * *`  | Property agent                                         | Add new contacts                                                       | I am able to contact new clients or agents                                       |
| `* * *`  | Property agent                                         | Clear all entries                                                      | Faster delete all contacts if necessary                                          |
| `* * *`  | Property agent                                         | List all my contact entries                                            | I can see my contact list in case I forget their names                           |
| `* * *`  | New user                                               | Learn all the commands available                                       | I know how to use the address book                                               |
| `* * *`  | Property agent                                         | Find contacts by address                                               | Easily locate contacts that stay in that area                                    |

### Use cases

(For all use cases below, the **System** is the `AddressBook` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Delete contacts that I no longer require**

**MSS**

1.  User requests to list persons
2.  AddressBook shows a list of persons
3.  User requests to delete a specific person in the list
4.  AddressBook deletes the person

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. AddressBook shows an error message.

      Use case resumes at step 2.

**Use case: Archive a contact**

**MSS**
1. User lists active contacts (`list`).
2. AddressBook shows active list.
3. User enters `archive INDEX`.
4. System marks the person archived and keeps the active list visible.

**Extensions**
* 3a. Index invalid → System shows error, use case resumes at step 2.
* 3b. Person already archived → System shows “This person is already archived.”, use case ends.

---

**Use case: Unarchive a contact**

**MSS**
1. User switches to archived list (`list archive`).
2. AddressBook shows archived list.
3. User enters `unarchive INDEX`.
4. System marks the person active and switches back to the active list.

**Extensions**
* 3a. Index invalid → System shows error, use case resumes at step 2.
* 3b. Person is not archived → System shows “This person is not archived.”, use case ends.

**Use case: View my contacts and schedule in one platform**

**MSS**

1.  User lists all contacts
2.  System displays all contacts and meetings associated with each contact

    Use case ends.

**Use case: Sort my contacts into categories (tagging)**

**MSS**

1. User lists all contacts by categories using command line
2. System displays contacts list sorted by categories specified

    Use case ends.

**Use case: Get reminded of meetings and appointments when they are approaching**

**MSS**

1. User opens application
2. Systems displays upcoming meetings at top of application page in a message bar upon startup

    Use case ends.

**Use case: Set reminders linked to specific contacts**

**MSS**

1. User searches for a certain contact
2. System displays details associated with contact
3. User adds reminder to contact using command line
4. System displays success message and reminder details

    Use case ends.

**Use case: Add dates for meetings **

**MSS**

1. User searches for a certain contact
2. System displays details associated with contact including meetings
3. User adds meeting date using command line
4. System displays success message and meeting details

    Use case ends.

**Use case: Search for contacts using partial names**

**MSS**

1. User searches for client using find command with a partial name
2. System retrieves and displays list of contacts containing partial query (case-insensitive))

    Use case ends.

**Use case: Search for contacts by tag**

**MSS**

1. User searches for contacts using find tag command with a tag keyword
2. System retrieves and displays list of contacts whose tags contain the keyword/partial keyword 

    Use case ends.

**Use case: Tag each client with their property location**

**MSS**

1. User lists all contacts or searches for client
2. System retrieves and displays list of contacts
3. User inputs command for tagging client location based on displayed index
4. System displays success message and location details

    Use case ends.

**Use case: Sort clients and agents by type**

**MSS**

1. User opens application
2. User requests to sort contacts by type
3. System prompts for sort criteria
4. User specifies criteria
5. System retrieves and displays filtered list of contacts matching the criteria

   Use case ends.

**Extensions**

* 1a. User specifies multiple criteria.
    * 1a1. System applies all specified filters.
    * 1a2. System displays contacts matching all criteria.

* 1b. No contacts match the selected type.
    * 1b1. System shows “No contacts found.”

**Use case: Set recurring reminders for contacts**

**MSS**

1. User opens application
2. User types client’s name into the search bar
3. System retrieves and displays contact
4. User requests to set a recurring reminder
5. System prompts for reminder details
6. User enters reminder details
7. System validates reminder details
8. System saves the recurring reminder and confirms creation
9. User views the reminder linked to the contact

   Use case ends.

**Use case: Link meeting to multiple contacts**

**MSS**

1. User opens application
2. User views contact list and selects contact
3. User requests to set a meeting for this contact
4. System prompts for meeting details
5. User enters meeting details, including other contacts involved
6. System validates meeting details and validates other contacts exist
7. System adds meeting details to the original contact
8. System adds meeting details for all specified attendees
9. System displays success message listing all contacts updated with the meeting

   Use case ends.

**Extensions**

* 6a. User enters invalid meeting details.
    * 6a1. System displays error message with correct format.
    * 6a2. User re-enters date/time.

      Use case resumes at step 6.

* 6b. One or more specified contacts do not exist.
    * 6b1. System displays error “Contact not found: [name].”
    * 6b2. User removes invalid name or cancels.

      Use case resumes at step 7 or ends.


**Use case: Tag contacts by transaction stage**

**MSS**

1. User views contact list or searches for a specific contact
2. System retrieves and displays contacts with their current details
3. User inputs command for tagging transaction stage
4. System updates the contact with the specified transaction stage tag
5. System displays success message and transaction details

   Use case ends.

**Use case: Color code tags and events**

**MSS**

1. User views contact list or searches for a specific contact
2. System retrieves and displays contacts with their current details
3. User inputs command for tagging and specifies color
4. System updates the contact with the specified tag and color
5. System displays success message

   Use case ends.

**Use case: Highlight overdue tasks and meetings**

**MSS**

1. User opens application
2. System scans all meetings and tasks for overdue items (past current date/time)
3. System displays overdue items highlighted at the top of the interface
4. User views the highlighted overdue items with associated contact details

   Use case ends.

**Use case: Sort contacts by latest meeting**

**MSS**

1. User opens application
2. User selects “Sort by → Meeting date”
3. System rearranges contacts by latest meeting date, with nearest meeting first

   Use case ends.

**Extensions**

* 2a. No contacts in list.
    * 2a1. System displays “Contact list empty.”

**Use case: Search for client by typing name**

**MSS**

Precondition: User has launched the app.

1. User types client’s name into the search bar
2. System retrieves and displays all matching clients in real time
3. User clicks on desired client
4. System displays the client’s profile and details

   Use case ends.

**Extensions**

* 1a. System detects no matching client
* 1b. System shows “No client found” and suggests adding new contact

**Use case: View upcoming meeting with nearest deadline first**

**MSS**

Precondition: User has meetings scheduled and is at the landing page of the app.

1. User selects “Sort by → Meeting Date”
2. System rearranges client list by meeting times, with nearest deadline at the top
3. User views the first item to check details (time, location, client notes, etc)

   Use case ends.

**Extensions**

* 2a. No meetings scheduled.
    * 2a1. System shows “No meetings scheduled” and suggests adding a meeting


**Use case: Sort contacts by dates added**

**MSS**

Precondition: User is at the landing page of the app and has existing list of contacts.

1. User selects “Sort by → Date Added”
2. System rearranges contacts chronologically, with oldest client on top to prioritise loyal customers

   Use case ends.

**Extensions**

* 1a. No contacts in list
* 1b. System displays “Contact list empty”

**Use case: Sort contacts by alphabetical order**

**MSS**

1. User opens the app
2. User selects “Sort by → Alphabetical Order”
3. System rearranges all contacts in A–Z order

   Use case ends.

**Extensions**

* 1a. No contacts in list
    * 1a1. System displays “Contact list empty”
* 1b. Duplicate names exist
    * 1b1. System sorts by secondary field (e.g., phone number or email)

**Use case: Edit contacts to add new information about them**

**MSS**

1. User opens app
2. User selects a contact from the list
3. System displays the contact’s profile and details
4. User selects “Edit” option
5. User enters new or updated information
6. System saves the updated contact information
7. User views the updated contact profile

   Use case ends.

**Extensions**

* 1a. No contacts in list
    * 1a1. System displays “Contact list empty”
* 2a. Contact not found
    * 2a1. System displays “Contact not found”
* 5a. User enters invalid information (e.g., invalid email format)
    * 5a1. System displays error message and prompts correction

**Use case: Add new contacts**

**MSS**

1. User opens app
2. User selects “Add Contact” option
3. System displays an empty contact form
4. User enters contact details (e.g., name, phone number, email)
5. System validates the entered information
6. System saves the new contact

   Use case ends.

**Extensions**

* 4a. Required fields missing (e.g., name not provided)
    * 4a1. System prompts user to fill in missing fields
* 4b. Invalid data entered (e.g., email format incorrect)
    * 4b1. System displays error message and prompts correction
* 4c. Duplicate contact detected
    * 4c1. System notifies user and suggests merging or editing

**Use case: Clear all entries**

**MSS**

1. User opens app
2. User selects “Clear All Contacts” option
3. System prompts user for confirmation
4. User confirms the action
5. System deletes all contacts from the address book
6. User views empty contact list

   Use case ends.

**Extensions**

* 2a. Contact list is already empty
    * 2a1. System displays “Contact list empty”
* 3a. User cancels the confirmation step
    * 3a1. System aborts the operation and keeps all contacts

**Use case: List all my contact entries**

**MSS**

1. User opens app
2. User selects “List All Contacts” option
3. System retrieves all saved contacts
4. System displays the full list of contact entries
5. User scrolls or navigates through the displayed list

   Use case ends.

**Extensions**

* 2a. Contact list is empty
    * 2a1. System displays “Contact list empty”

**Use case: Learn all the commands available**

**MSS**

1. User opens app
2. User enters `help` command or selects the "Help" option from the menu
3. System opens the User Guide in the user's default web browser
4. User reviews the guide to understand available commands and their usage

   Use case ends.

**Extensions**

* 2a. User specifies a topic (e.g. `help add`, `help edit`)
    * 2a1. System opens the User Guide directly at the corresponding section for that command
* 3a. Browser cannot be opened (e.g. headless environment or OS restriction)
    * 3a1. System displays a small in-app "Help" window with the User Guide link and an option to copy it manually

**Use case: Find contacts by address**

**MSS**

1. User requests to find contacts by specifying an address keyword.

2. AddressBook filters and displays all contacts whose address contains the specified keyword.

3. User views the list of matched contacts.

   Use case ends.

**Extensions**

* 2a. No contact’s address matches the given keyword.

    * 2a1. AddressBook shows a message indicating that no contacts were found.

* 1a. User enters an invalid or empty address keyword.

    * 1a1. AddressBook displays an error message prompting the user to provide a valid keyword.

### Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2. Works fully offline
3. Should be able to hold up to `1000` persons without a noticeable sluggishness in performance for typical usage.
4. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
5. All user data should be persistent and survives app restarts and OS reboots.
6. Backups are stored locally
7. Data storage must be human editable text storage
8. Runs without installer, double-clickable JAR.
9. Usable at `1280×720` (`150%` scale) and higher, optimised for `1920×1080` and higher (`100/125%` scale).
10. All commands operable via typing/shortcuts (keyboard first)
11. Support partial matches and case-insensitive search
12. Product ≤ `100MB`, PDFs ≤ `15MB`, no unnecessary assets
13. Dataset size tested up to `2000` contacts
14. Clear separation of UI/Logic/Model/Storage

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Command**: A typed instruction entered by the user to interact with the app (e.g. add, delete, find, clear)
* **Contact**: An entry in the system that stores the details of a person (name, phone number, email, address, (optional) relationship with the person or stage of transaction with the person). May represent clients and vendors
* **Tag**: A label assigned to a contact to classify them by relationship (client or vendor) and/or transaction stage (prospect, negotiating or closed)
* **Relational Tag**: An optional tag to denote the contact’s relationship with the user, for example, client or vendor
* **Client**: A contact type representing a property buyer, seller, or tenant managed by the property agent
* **Vendor**: A contact type representing third-party partners that a property agent works with during transactions (e.g. contractors, lawyers, service providers)
* **Transaction Stage Tag**: An optional tag which denotes the phase of a property deal, for example, prospect, negotiating and closed
* **Prospect**: A potential client or deal that has just been identified or contacted, but no agreement or commitment has been made yet. Used as a tag to track early-stage commitments
* **Negotiating**: A deal in progress where the property agent and client are actively discussing terms, prices or conditions. Tagged to indicate an ongoing transaction that requires a follow-up
* **Closed**: A completed deal where the transaction has been finalised (e.g. property successfully bought, sold or rented). Tagged to distinguish finished transactions from active ones
* **Reminder**: A system notification that alerts the agent about upcoming events such as meetings, deadlines or tasks
* **Meeting**: A scheduled event linked to one or more contacts, representing professional events such as property showings, negotiations or check-ins
* **Human Editable Text Storage**: Data stored in plain text format (e.g. JSON, TXT) so users can open and manually edit it outside the app if needed
* **Double-clickable JAR**: A Java Archive file packaged so that the application can be run directly by double-clicking, without needing an installer
* **Partial Match Search**: A search feature that allows results to be found even if the user types only part of a name, address or tag. The search is case-insensitive and returns all matching entries containing the keyword substring.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Archiving a person

1. Prerequisites: Show active list with `list`. Ensure at least one person exists.
2. Test case: `archive 1`  
   Expected: “Archived: <name>”. Person disappears from active list.
3. Test case: `archive 1` again  
   Expected: Error “This person is already archived.”
4. Test case: `archive 0`, `archive x` (x > list size)  
   Expected: Invalid index error.

### Unarchiving a person

1. Prerequisites: Switch to archived list with `list archive`. Ensure it is non-empty (archive someone first).
2. Test case: `unarchive 1`  
   Expected: “Unarchived: <name>”. View switches back to active list and the person is visible there.
3. Test case: `unarchive 1` again (from active list)  
   Expected: Error “This person is not archived.”
4. Test case: `unarchive 0`, `unarchive x`  
   Expected: Invalid index error.

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
