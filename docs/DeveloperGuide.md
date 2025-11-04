---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# Homey Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }
* The `Add Meeting` feature was inspired by the add command in [AB3](https://github.com/nus-cs2103-AY2526S1/tp).
While the parsing and model integration logic were adapted from AB3’s `AddCommand` and `AddCommandParser`, the meeting-related components 
such as the Meeting class, meeting validation logic, and enhanced success feedback were independently designed.

* Parts of the Javadoc documentation for the features Help, Archive, Unarchive, ListMeeting, Meeting, FindByAddress, and EditMeeting,
as well as their related files, were written with assistance from [ChatGPT (OpenAI)](https://chat.openai.com).
The generated content was reviewed and adapted by our team to ensure accuracy and consistency with the project’s coding and documentation standards.

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

**`Main`** is the core component responsible for launching and shutting down the application.
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
The sequence diagram below shows how the `HelpCommand` interacts with the UI to open the User Guide or display the fallback Help window.

<puml src="diagrams/help/HelpSequence.puml" width="720" />

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

#### Interactive Command Support

The Logic component has been enhanced to support interactive commands that can prompt users for missing information.

How interactive commands work:

1. When a command with missing required fields is parsed:
    * The parser creates a command object in interactive mode as indicated by a boolean flag `isInteractive`
    * Missing fields are tracked in a mutable map `missingFields`
    * Placeholder values are used for the partial object as specified by the class `PlaceholderPerson`

2. During command execution:
    * If the command is interactive, it returns a prompt for the next missing field
    * The command stays active in LogicManager and is handled using `handleInteractiveResponse` until completed
    * Each user response updates the command's internal state
    * The input `cancel` will terminate any ongoing interactive command

The sequence diagram below shows how an interactive add command flows through the system:

<puml src="diagrams/InteractiveAddSequenceDiagram.puml"/>

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores Homey's data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
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
* can save both Homey's data and user preference data in JSON format, and read them back into corresponding objects.
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

### Find Feature

#### Overview
The Find feature allows users to search for contacts using various criteria including name, address, tags, relation type, and transaction stage. It supports both partial matching (for name, address, tags) and exact matching (for relation and transaction stage).

#### Implementation

The Find feature is implemented through the following key components:

**Class Structure**

<puml src="diagrams/find/FindClass.puml" width="550"/>

The diagram above shows the main classes involved in the Find feature:
* `FindCommandParser` – Parses user input and creates the appropriate predicate based on the prefix used (none for name, `a/` for address, `t/` for tags, `r/` for relation, `s/` for transaction stage)
* `FindCommand` – Executes the find operation by applying the predicate to filter the person list
* `XYZContainsKeywordsPredicate` – Represents the family of predicate classes that implement the actual filtering logic

#### Input Validation
The `FindCommandParser` validates input based on search type:
* **Relation search**: Only accepts 'client' or 'vendor'
* **Transaction stage search**: Only accepts 'prospect', 'negotiating', or 'closed'
* **Other searches**: Accept any keywords

**Execution Flow**

<puml src="diagrams/find/FindSequence.puml" width="620"/>

The sequence diagram above illustrates how a find command is processed:

1. The user enters a find command (e.g., `find alice`)
2. `FindCommandParser` validates and parses the input, creating the appropriate predicate
3. A `FindCommand` is created with the predicate
4. When executed, `FindCommand` updates the model's filtered list using the predicate
5. The UI automatically reflects the filtered results

#### Error Handling
* Invalid relation: "Invalid relation. Only 'client' or 'vendor' are allowed"
* Invalid transaction stage: "Invalid transaction stage. Only 'prospect' or 'negotiating' or 'closed' are allowed"
* Empty search: Shows usage message for the specific search type

#### Design Pattern: Strategy Pattern
The find command uses the Strategy Pattern where different predicate classes implement the same `Predicate<Person>` interface. This allows the `FindCommand` to work with any predicate without knowing the specific filtering logic.

**Key Benefits:**
* **Extensibility**: Easy to add new search types
* **Maintainability**: Each predicate handles its own logic
* **Polymorphism**: Same interface for all predicates

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current Homey state in its history.
* `VersionedAddressBook#undo()` — Restores the previous Homey state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone Homey state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial Homey state, and the `currentStatePointer` pointing to that single Homey state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in Homey. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of Homey after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted Homey state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified Homey state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the Homey state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous Homey state, and restores Homey to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial Homey state, then there are no previous Homey states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores Homey to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest Homey state, then there are no undone Homey states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify Homey, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all Homey states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire Homey data state.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### Data archiving / unarchiving

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

### List contacts by meeting date and time

#### Implementation

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

<puml src="diagrams/meeting/ListMeetingClassDiagram.puml" width="500" />

#### Example Usage

Given below is an example usage scenario and how the feature behaves at each step.

Step&nbsp;1. The user launches the application and types the command `list meeting`.

Step&nbsp;2. `LogicManager` calls `AddressBookParser#parseCommand("list meeting")`.  
The `AddressBookParser` passes the argument `"meeting"` to `ListCommandParser`.

Step&nbsp;3. `ListCommandParser` recognizes the argument and returns a new instance of `ListMeetingCommand`.

Step&nbsp;4. `LogicManager` executes the command, which calls `model.updateFilteredPersonList(predicate)` and `model.sortFilteredPersonListBy(comparator)` to filter and order the contacts by meeting time.

Step&nbsp;5. The UI (`PersonListPanel`) automatically refreshes to display only the persons with meetings, sorted from earliest to latest.

The following sequence diagram shows how the `list meeting` command flows through the `Logic` component:

<puml src="diagrams/meeting/ListMeetingSequenceDiagram.puml" width="520" />

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

<puml src="diagrams/meeting/ListMeetingModelDiagram.puml" width="500" />

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

**DevOps Rationale:**  
The DevOps setup follows the AB3 baseline with adaptations for the Homey project.  
Continuous Integration (CI) is managed via GitHub Actions, which runs tests and style checks on every push and pull request.  
This ensures consistent build quality across team members.  
The application is packaged as a single runnable JAR for portability and simplicity, following AB3’s Gradle setup.  
All tests can be executed via `gradlew check`.  
Log files are written to the local directory to support offline debugging.  
Configuration and data storage remain in JSON to preserve human-editability.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**: Property Agent

* Manages a large number of client and stakeholder contacts
* Prefers desktop applications over mobile alternatives
* Has a fast typing sped
* Prefers keyboard input to mouse interaction
* Is comfortable using Command-Line Interface (CLI) applications

**Value proposition**: 

**Problem Statement**
Property agents often struggle to manage contacts, meetings, and transactions across multiple, 
unconnected platforms. Meetings tracked in separate applications are easily forgotten, 
while contact lists become difficult to organise and navigate.
Furthermore, grouping stakeholders by location or transaction stage is often tedious and time-consuming.

**Solution**
Homey provides a centralized platform that enables property agents to efficiently track, organize, and schedule contacts. 
It consolidates essential functions such as meeting visibility, contact tagging, and transaction tracking within a single interface.

**Scope and Limitations**
Homey focuses on contact and meeting management for property agents. It does not provide:
* Legal or financial tracking functionalities
* Full-fledged Customer Relationship Management (CRM) features
* Marketing, listing, or property advertisement management tools

**User Persona**:

**Profile**
A working professional and university graduate managing a large client base. 
They are experienced, independent, and often collaborate with fellow agents. 
Due to frequent travel between properties, 
they require a lightweight and convenient solution that keeps their workflow synchronised across devices.

**Behavioural Traits**
* Prefers typing commands over using a mouse
* Appreciates structured data organization and quick search capabilities
* Occasionally forgetful due to a heavy client load and overlapping meetings
* Values efficiency and minimal disruption while managing clients on the move

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a ...                                               | I want to ...                                                        | So that I can ...                                                              |
|----------|--------------------------------------------------------|----------------------------------------------------------------------|--------------------------------------------------------------------------------|
| `* *`    | property agent                                         | view my contacts and meetings in one platform                        | manage my meetings and contacts together seamlessly                            |
| `* *`    | property agent with many clients and colleagues        | sort my contacts into categories (tagging)                           | easily remember my relationship with them                                      |
| `*`      | busy property agent                                    | view my meetings and contact list on-the-go                          | easily access necessary information when I’m outside or in a rush              |
| `*`      | property agent                                         | add notes visible only to me                                         | keep private thoughts while still sharing client basics                        |
| `* *`    | property agent managing clients                        | sort clients and vendors by type                                     | filter out which types of deals I want to focus on at any point of time        |
| `*`      | property agent                                         | attach notes to each contact                                         | remember important details                                                     |
| `* *`    | property agent                                         | tag contacts by transaction stage like prospect, negotiating, closed | track progress easily                                                          |
| `* *`    | property agent                                         | set overdue meetings highlighted                                     | prioritise catching up quickly                                                 |
| `* *`    | property agent                                         | add dates for meetings                                               | keep track of important events and attend them                                 |
| `* *`    | property agent that wants to track their current deals | sort contacts by earliest meeting                                    | prioritise deals or meetings that have been delayed or are taking too long     |
| `* *`    | forgetful property agent                               | search for contacts using partial names                              | find their contact information despite not remembering their full name         |
| `*`      | property agent                                         | input details for a new contact in one line                          | add contacts more conveniently                                                 |
| `* *`    | property agent who handles many meetings               | attach each client with their property location                      | remember where they live                                                       |
| `* * *`  | property agent who prefers typing                      | search for client by typing their name                               | save time instead of scrolling through the entire Homey contact list           |
| `* *`    | property agent managing multiple tasks                 | view upcoming meeting with the nearest deadline first                | know which client to attend to next                                            |
| `*`      | property agent juggling many deals                     | attach notes to each client’s profile                                | remember key details of past conversations                                     |
| `*`      | property agent                                         | write multiple different commands to do the same thing               | operate faster as I don’t have to remember specific syntax for each command    |
| `*`      | property agent with a lot of contacts                  | archive contacts that have completed deals                           | prioritise contacts that I have ongoing deals with                             |
| `*`      | forgetful user                                         | write simpler commands intuitively and when prompted to              | operate faster as I don’t have to remember the complex syntax for each command |
| `* *`    | property agent                                         | edit contacts to add new information about them                      | can keep my contacts updated without deleting and creating a new one           |
| `* * *`  | property agent                                         | delete contacts that I no longer require                             | have a less cluttered contact list that is easier to navigate                  |
| `* * *`  | property agent                                         | add new contacts                                                     | contact new clients or agents                                                  |
| `* * *`  | property agent                                         | clear all entries                                                    | faster delete all contacts if necessary                                        |
| `* * *`  | property agent                                         | list all my contact entries                                          | see my contact list in case I forget their names                               |
| `* * *`  | new user                                               | learn all the commands available                                     | know how to use Homey                                                          |
| `* * *`  | property agent                                         | find contacts by address                                             | easily locate contacts that stay in that area                                  |
| `* *`    | property agent juggling multiple stakeholders          | edit or delete a meeting                                             | quickly update or remove meeting                                               |

### Use cases

(For all use cases below, the **System** is `Homey` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Delete contacts that I no longer require**

**MSS**

1.  User requests to list persons
2.  Homey shows a list of persons
3.  User requests to delete a specific person in the list
4.  Homey deletes the person
                          
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
2. Homey shows active list.
3. User enters `archive INDEX`.
4. System marks the person archived and keeps the active list visible.

**Extensions**
* 3a. Index invalid → System shows error, use case resumes at step 2.
* 3b. Person already archived → System shows “This person is already archived.”, use case ends.

**Use case: Unarchive a contact**

**MSS**
1. User switches to archived list (`list archive`).
2. Homey shows archived list.
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

**Use case: Add dates for meetings**

**MSS**

1. User opens the application.
2. User adds a contact with a meeting date and time.
3. System validates the meeting input format (`yyyy-MM-dd HH:mm`).
4. System updates the contact with the new meeting date and time.
5. System displays a success message confirming the scheduled meeting.

   Use case ends.

**Extensions**

* **3a.** User enters an invalid meeting format.
    * 3a1. System displays an error message with the correct format (e.g., “Meetings must follow the format YYYY-MM-DD HH:mm”).
    * 3a2. User re-enters the correct date and time.
        * Use case resumes at step 6.

* **64b.** User tries to add a meeting for a non-existent contact index.
    * 6b1. System displays “Invalid person index.”
    * Use case ends.

**Use case: Search for contacts using partial names**

**MSS**

1. User searches for client using find command with a partial name
2. System retrieves and displays list of contacts containing partial query (case-insensitive))

    Use case ends.

**Use case: Search for contacts by tag**

**MSS**

1. User searches for contacts using find tag command with a tag keyword
2. System retrieves and displays list of contacts whose tags contain the keyword/partial keyword 
3. User views the list of contacts

    Use case ends.

**Use case: Search for contacts by relation**

**MSS**

1. User searches for contacts using find relation command with a relation keyword
2. System retrieves and displays list of contacts whose relation matches the keyword
3. User views the list of contacts

   Use case ends.

**Use case: Search for contacts by transaction stage**

**MSS**

1. User searches for contacts using find transaction stage command with a transaction keyword
2. System retrieves and displays list of contacts whose transaction stage matches the keyword
3. User views the list of contacts

   Use case ends.

**Use case: Attach each client with their property location**

**MSS**

1. User lists all contacts or searches for client
2. System retrieves and displays list of contacts
3. User inputs command for attaching client location based on displayed index
4. System displays success message and location details

    Use case ends.

**Use case: Sort clients and vendors by type**

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

**Use case: Highlight overdue meetings**

**MSS**

1. User opens application
2. System scans all meetings for overdue items (past current date/time)
3. System displays overdue items highlighted at the top of the interface
4. User views the highlighted overdue items with associated contact details

   Use case ends.

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

**Precondition:** User has at least one contact with a scheduled meeting.

1. User enters the command `list meeting`.
2. System filters all contacts with scheduled meetings.
3. System sorts the contacts by meeting date and time in ascending order (earliest first).
4. If two meetings share the same date and time, the contacts are sorted alphabetically by name.
5. System displays the list of contacts with meetings, showing the nearest upcoming meeting first.
6. User selects a contact to view full meeting and contact details in the right panel.

   Use case ends.

**Extensions**

* **1a.** User enters the command in a different case (e.g., `list Meeting` or `LIST MEETING`).
    * 1a1. System recognises the command and performs the same action.
    * Use case resumes at step 2.

* **2a.** No contacts have meetings scheduled.
    * 2a1. System displays a clear message: “No contacts with meetings found.”
    * Use case ends.

* **3a.** User is currently viewing the archived list.
    * 3a1. System only considers active contacts and excludes archived ones.
    * Use case resumes at step 5.

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
5. System deletes all contacts from Homey
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
2. User selects "List All Contacts" option
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

1. User enters a command to find contacts by specifying one or more address keywords (e.g., `find a/Bedok`).
2. System filters and displays all contacts whose addresses contain any of the specified keywords.
3. User views the list of matched contacts, displayed in the main window.

   Use case ends.

**Extensions**

* **1a.** User enters an empty or invalid address keyword.
    * 1a1. System displays an error message prompting the user to enter at least one valid keyword.
    * Use case ends.

* **2a.** No contact’s address matches the given keyword(s).
    * 2a1. System displays a message indicating that no contacts were found.
    * Use case ends.

* **2b.** User provides multiple keywords (e.g., `find a/bedok north`).
    * 2b1. System returns all contacts whose addresses contain **any** of the given keywords, regardless of order or case.
    * Use case resumes at step 3.

**Use case: Edit or remove meetings**

**MSS**

1. User opens the application.
2. User views the contact list.
3. User selects a contact to edit.
4. User requests to edit the meeting details for that contact.
5. System prompts for meeting input.
6. User provides the new meeting date/time or leaves it blank to clear the meeting.
7. System validates the meeting format (if provided).
8. System updates or removes the meeting for the selected contact.
9. System displays a success message confirming the update or removal.

   Use case ends.

**Extensions**

* **6a.** User enters an invalid meeting format.
    * 6a1. System displays an error message with the correct format (e.g., “yyyy-MM-dd HH:mm”).
    * 6a2. User re-enters the meeting details.
        * Use case resumes at step 7.

* **8a.** User tries to clear a meeting that does not exist.
    * 8a1. System displays message: “No meetings to clear for [contact name].”
    * Use case ends.

**Use case: Change the transaction stage of a contact**

**MSS**

1. User requests to change the transaction stage of a contact by specifying the contact's index and the new transaction stage.
2. Homey validates that the specified index exists and the transaction stage is valid.
3. Homey updates the contact's transaction stage.
4. Homey displays a success message confirming the update.

    Use case ends.

**Extensions**

* 1a. User changes the transaction stage using the `edit` command instead of `transaction`
  * Steps 2-4 proceed identically but other fields can also be modified alongside the transaction stage. 
* 1b. User enters an invalid command format or omits required fields (e.g. missing index or `s\`).
  * 1b1. Homey displays "Invalid command format!" and the correct command usage details.
* 2a. The given index is invalid.
  * 2a1. If the index is non-positive, Homey displays "Invalid command format!" and indicates that the index must be positive.
  * 2a2. If there are no contacts with that index, Homey displays "The person index provided is invalid".
* 2b. The transaction stage provided is empty or invalid (i.e. not one of `prospect`, `negotiating` or `closed`).
  * 2b1. If the transaction stage provided is empty, Homey displays "Invalid command format! Transaction stage cannot be empty."
  * 2b1. If the transaction stage provided is invalid, Homey displays the list of valid stages.

**Use case: Editing a remark**

**MSS**

1. User requests to change the remark of a contact by specifying the contact's index and the new remark.
2. Homey validates that the specified index exists and the remark length does not exceed 100 characters.
3. Homey updates the contact's remark.
4. Homey displays a success message confirming the update.

   Use case ends.

**Extensions**

* 1a. User changes the remark using the `edit` command instead of `remark`
  * 1a1. Steps 2-4 proceed identically but other fields can also be modified alongside the transaction stage.
  * 1a2. If the remark exceeds 100 characters, Homey displays "Remark cannot exceed 100 characters."
* 1b. User enters an invalid command format or omits required fields (e.g. missing index or `rm/`).
  * 1b1. Homey displays "Invalid command format!" and the correct command usage details.
* 2a. The given index is invalid.
  * 2a1. If the index is non-positive, Homey displays "Invalid command format!" and indicates that the index must be positive.
  * 2a2. If there are no contacts with that index, Homey displays "The person index provided is invalid".
* 2b. The remark provided is invalid (i.e. more than 100 characters).
  * Homey displays "Invalid command format! Remark cannot exceed 100 characters."

### Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2. Works fully offline
3. With a data file of 1000 contacts, all CRUD operations and search commands should execute and render results in under 500ms on typical hardware.
4. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
5. All user data should be persistent and survives app restarts and OS reboots.
6. Backups are stored locally
7. Data storage must be human editable text storage
8. Runs without installer, double-clickable JAR.
9. Usable at `1280×720` (`150%` scale) and higher, optimised for `1920×1080` and higher (`100/125%` scale).
10. All commands operable via typing/shortcuts (keyboard first)
11. Search operations must be flexible, supporting partial word matches and case-insensitive queries for improved user experience.
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
* **CRUD**: The four basic data operations - Create (add), Read (list/find), Update (edit/remark/transaction), and Delete (delete/clear)

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

### Viewing Help



### Interactive add

1. Test case: `add`  
   Expected: Prompts for name, phone, email, address and transaction stage fields. Adds corresponding person based on your inputs.
2. Test case: `add rm/This is a test. m/2025-12-01 00:00`  
   Expected: Prompts for name, phone, email, address and transaction stage fields. Adds corresponding person with the given remark and meeting.
3. Test case: `add`, then `cancel`  
   Expected: Prompts for name, then aborts command.


### Changing relational tag

1. Prerequisites: Ensure at least one person exists in the displayed list.
2. Test case: `relation 1 vendor`  
   Expected: "Added relation vendor to Person: ...". Changes relation of first contact to be vendor.
2. Test case: `relation 0 client`  
   Expected: Error "Invalid command format!"
3. Test case: `relation 1 friend`  
   Expected: Error "Relation should be 'client' or 'vendor'."


### Changing transaction stage

1. Prerequisites: Ensure at least one person exists in the displayed list.
2. Test case: `transaction 1 s/closed`  
    Expected: "Added transaction stage to Person: ...; Transaction: [closed]; ..."  
    The person's transaction stage tag displays `closed`.
3. Test case: `transaction 1 s/invalidstage`  
    Expected: Error "Transaction stage should be 'prospect', 'negotiating' or 'closed'."
4. Test case: `transaction x s/closed` (x <= 0 or x > list size)  
    Expected: Invalid index error.


### Adding a remark

1. Prerequisites: Ensure at least one person exists in the displayed list.
2. Test case: `remark 1 rm/Likes nature`  
   Expected: "Added remark to Person: ...; Remarks: Likes nature; ..."
   The person's remark field displays "Likes nature".
3. Test case: `remark 1 rm/`  
   Expected: "Removed remark from Person: ...; Remarks:;" The person's remark field is no longer displayed.
4. Test case: `remark 1 s/<STRING>` where `STRING` has more than 100 characters.  
   Expected: Error "Invalid command format! Remark cannot exceed 100 characters"
5. Test case: `transaction x s/closed` (x <= 0 or x > list size)  
   Expected: Invalid index error.



### Editing a contact's meeting

1. Prerequisites: Ensure at least one contact exists by using `list`.
2. Test case: edit 1 m/2025-11-03 14:00  
   Expected: “Updated meeting for Kevin Tan: 2025-11-03 14:00” Meeting field is added to contact card
3. Test case: edit 1 m/  
   Expected: “Cleared meeting for Kevin Tan.” Meeting field is removed from the contact card.
4. Test case: edit 1 m/invalid-date  
   Expected: Error “Meeting must be in yyyy-MM-dd HH:mm (24h) format and be a real date/time, e.g. 2025-11-03 14:00.”

### Listing contacts by meeting date

1. Prerequisites: Ensure at least two contacts have meetings set using `list meeting`.
2. Test case: list meeting  
   Expected: Displays only contacts with meetings, sorted by earliest meeting first.
3. Test case: Run list meeting when no contacts have meetings.  
   Expected: Empty list message such as “No contacts with meetings found.”
4. Test case: list Meeting or list MEETING  
   Expected: "Updated meeting for Kevin Tan: 2025-11-11 09:30" (Will still work)

### Finding contacts

#### Find by name

1. Prerequisites: List all persons with `list`. Multiple persons should be visible.
2. Test case: `find john`
   Expected: Shows contacts with names containing "john". 
3. Test case: `find john alex`
   Expected: Shows contacts containing "john" OR "alex" in their names.
4. Test case: `find` or `find    `
   Expected: Error "Invalid command format!" with usage instructions.

#### Find by address

1. Prerequisites: Ensure contacts have different addresses.
2. Test case: `find a/bedok`
   Expected: Shows all contacts with addresses containing "bedok".
3. Test case: `find a/`
   Expected: Error with address-specific usage message.

#### Find by tag

1. Prerequisites: Ensure contacts have various tags.
2. Test case: `find t/friend`
   Expected: Shows all contacts tagged with "friend".
3. Test case: `find t/`
   Expected: Error with tag-specific usage message.
4. Test case: `find t/friend_buyer`
   Expected: Error "Invalid keyword. Tags can only contain alphanumeric characters".

#### Find by relation

1. Prerequisites: Ensure you have both vendors and clients in the list.
2. Test case: `find r/client`
   Expected: Shows all contacts with relation "client".
3. Test case: `find r/supplier`
   Expected: Error "Invalid relation. Only 'client' or 'vendor' are allowed".
4. Test case: `find r/client vendor`
   Expected: Error "Relation search only accepts one keyword"

#### Find by transaction stage 

1. Prerequisites: Ensure contacts have different transaction stages.
2. Test case: `find s/prospect`
   Expected: Shows all contacts with transaction stage "prospect".
3. Test case: `find s/pending`
   Expected: Error "Invalid transaction stage. Only 'prospect' or 'negotiating' or 'closed; are allowed".
4. Test case: `find s/prospect closed`
   Expected: Error "Transaction stage search only accepts one keyword"

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

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Effort**

* **Difficulty level**
  * Moderate to high
* **Challenges faced**
  * Ensuring meeting commands interacted correctly with existing features without breaking core functionality
  * Ensuring interactive commands did not break functionality for other commands and could work seamlessly with multiple steps
  * Designing command logic that allows flexible `remark` editing while maintaining input validation rules (e.g. length limit and empty `remark` handling).
  * Ensuring the `transaction` command correctly validates and updates only valid stages without affecting unrelated data.
  * Ensuring the `help` command worked seamlessly in both online and offline modes without causing UI freezes or blocking the main thread.
  * Handling environments that block browser access - needed fallback logic for the in-app Help window.
  * Managing URL anchor mappings so that help [TOPIC] always deep-links to the correct User Guide section, even when document headings change.
  * Implementing `archive` and `unarchive` without breaking existing `list`, `find`, and `delete` commands - required careful state tracking between active and archived lists.
  * Maintaining index consistency after archiving/unarchiving, since both lists use different data views.
  * Preventing users from accidentally interacting with archived contacts (e.g., blocking `edit` and `find` actions until unarchived).
  * Extending the find command to support multiple search types (name, address, tag, relation, transaction stage) with proper validation and error handling for each type.
  * Completely redesigning the GUI from AB3's basic list view to a modern two-panel layout with detailed contact preview, requiring significant restructuring of UI components and styling.
* **Effort required:**
  * Implemented new logic for `Meeting` class and integrated it with `AddCommand`, `EditCommand`, and `ListMeetingCommand`.
  * Implemented new logic for `InteractiveCommand` and adapted `LogicManager` for interactive add command.
  * Features `relation`, `transaction` and `remark` are adapted from AB3's [add command tutorial](https://se-education.org/guides/tutorials/ab3AddRemark.html) and enhanced with improved code quality
  * Implemented RemarkCommand, RemarkCommandParser, and Remark to support adding, editing, and deleting remarks with instant UI updates. Added 100-character validation and error handling, with tests (RemarkTest, RemarkCommandParserTest) for edge cases.
  * Implemented Transaction, TransactionCommand and TransactionCommandParser to handle stage updates with validation and real-time UI reflection. Added tests (TransactionCommandParserTest) to ensure correct error handling for invalid inputs.
  * Created the HelpCommand, HelpWindow, and integrated URL anchor mapping for deep-link support.
  * Added fallback logic and event handling to open the offline Help window when the browser cannot be launched.
  * Implemented `archive` and `unarchive` commands with bidirectional state updates between the active and archived contact lists.
  * Added validation checks and user feedback messages for restricted operations on archived contacts.
  * Thoroughly tested UI behavior to ensure commands like `find` and `edit` interacted correctly with the new archive system.
  * Extended AB3's basic find command to support searching across multiple fields with prefix-based syntax. Extended FindCommandParser with field-specific validation, single/multiple keyword support, and comprehensive error messages to prevent invalid search combinations.
  * Redesigned the GUI from AB3's original layout into a modern interface with:
    * Two-panel split view (contact list + detailed preview)
    * Enhanced contact cards displaying relation and transaction stage badges, with meeting information integrated directly into each card
    * Dynamic color-coding system that turns meeting information red when the scheduled time has passed
    * Detailed contact preview panel showing all information in a structured, readable format
    * Better visual hierarchy and information organisation
* **Achievements of project:**
  * Successfully extended into a property-agent focused app supporting meeting scheduling, editing, and listing.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Planned Enhancements**

**Team size:** 5 members. 

1. **Allow names with apostrophes, hyphens, and slashes to be added correctly:**
Currently, users cannot add contacts whose names contain special characters such as apostrophes (O'Neill), hyphens (Anne-Marie), or slashes (Muhammad s/o Rahman).
This happens because the name validation rejects these characters, and in the case of slashes, the command parser interprets them as prefixes (e.g., s/).
We plan to relax the name validation rules and refine the command parser to correctly handle such names. For example, the command `add n/"Muhammad s/o Rahman" p/91234567 e/m.rahman@example.com` will successfully add the contact instead of producing an invalid command error.


2. **Allow multiple contacts with identical names to be added correctly:**  
Currently, Homey prevents users from adding a new contact if another contact with the same name already exists. This restriction forces users to artificially modify names (e.g., “John Tan 1”, “John Tan 2”) to differentiate between different clients or vendors, which reduces data clarity and makes the contact list less natural to use.
We plan to relax this restriction by identifying contacts internally using unique identifiers instead of relying on the name field for equality checks. This will involve modifying the duplicate detection logic in the `AddCommand` and `AddressBook` classes to compare contacts based on their unique IDs rather than their names.
For example, users will be able to add both “John Tan” (buyer) and “John Tan” (vendor) as separate contacts without encountering a duplicate contact error.


3. **Allow multiple phone numbers and email addresses for a single contact:**  
Currently, each contact in Homey can only store one phone number and one email address. This limits flexibility, as many clients or vendors may have multiple contact channels (e.g., personal and work numbers, or separate emails for correspondence and billing).
We plan to enhance Homey’s contact model to support multiple phone numbers and email addresses per contact. This will involve updating the `Person` class to store lists of phone numbers and email addresses, modifying the `add` and `edit` command parsers to accept multiple entries, and adjusting the UI and storage components to display and save them correctly.
For example, users will be able to add a contact such as:  
`add n/Alex Tan p/91234567 p/97865432 e/alex@gmail.com e/alex.tan@company.com`  
to store both numbers and emails under a single contact.


4. **Prevent total data loss on startup due to a single invalid field:**  
Currently, when Homey starts up and encounters a single invalid field (e.g., a corrupted phone number or improperly formatted date) in the saved data file, the entire address book fails to load. This results in complete data loss until the user manually fixes or deletes the corrupted file.
We plan to improve the data loading mechanism to perform **partial recovery** instead of rejecting the entire dataset.
Specifically, Homey will:
   - Skip only the affected entries containing invalid fields.
   - Load all remaining valid contacts into the application.
   - Display a clear error message indicating which records were skipped and why.
   
   This ensures that users do not lose all their saved contacts due to one malformed entry and improves overall data resilience.


5. **Make error message for `relation` more specific:**
Currently, the error message for `relation` is:
`Invalid command format!
relation: Edits relation tag of the person identified by the index number used in the last person listing. Existing relation tag will be overwritten.
Parameters: INDEX (must be a positive integer) [client/vendor]
Example: relation 1 client`, including redundant information which forces users to read through a verbose message before finding the relevant information.
We plan to make the error message more targeted by reducing the message content to:
`Invalid command format!
Parameters: INDEX (must be a positive integer) [client/vendor]
Example: relation 1 client`.


6. **Make error message for `remark` more specific:**
Currently, the error message for `remark` is:
`Invalid command format! 
remark: Edits the remark of the person identified by the index number used in the last person listing.
Parameters: INDEX (must be a positive integer) rm/[REMARK] (must not be more than 100 characters)
Example: remark 1 rm/Prefers properties in the East.`, including redundant information which forces users to read through a verbose message before finding the relevant information.
We plan to make the error message more targeted by reducing the message content to:
`Invalid command format! 
Parameters: INDEX (must be a positive integer) rm/[REMARK] (must not be more than 100 characters)
Example: remark 1 rm/Prefers properties in the East.`.


7. **Make error message for `transaction` more specific:**
Currently, the error message for `transaction` is:
`Invalid command format! 
transaction: Edits the transaction stage of the person identified by the index number used in the last person listing. Existing transaction stage will be overwritten by the input.
Parameters: INDEX (must be a positive integer) s/ TRANSACTION STAGE
Example: transaction 1 s/prospect`, including redundant information which forces users to read through a verbose message before finding the relevant information.
We plan to make the error message more targeted by reducing the message content to:
`Invalid command format! 
Parameters: INDEX (must be a positive integer) s/ TRANSACTION STAGE
Example: transaction 1 s/prospect`.


8. **Make “no results” message for find a/ more specific:**
Currently, when a user searches for an address using find a/ and there are no matching contacts, Homey displays a generic message:
`0 persons listed!`
This message does not clearly indicate that the search was performed using the address field, which may confuse users who expect address-specific feedback.
We plan to make the message more targeted by updating it to:
`No contacts found in the specified area.`
This enhancement improves clarity and user experience by providing context-aware feedback that directly reflects the user’s search intent when using `find a/`.
--------------------------------------------------------------------------------------------------------------------

