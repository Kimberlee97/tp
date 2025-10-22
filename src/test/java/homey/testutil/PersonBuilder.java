package homey.testutil;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
import homey.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "placeholder";
    public static final String DEFAULT_PHONE = "00000000";
    public static final String DEFAULT_EMAIL = "placeholder@xx.xx";
    public static final String DEFAULT_ADDRESS = "placeholder";
    public static final String DEFAULT_RELATION = "client";
    public static final String DEFAULT_STAGE = "prospect";
    public static final String DEFAULT_REMARK = "Likes nature.";

    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private Relation relation;
    private TransactionStage stage;
    private Set<Tag> tags;
    private Optional<Meeting> meeting;
    private Remark remark;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        relation = new Relation(DEFAULT_RELATION);
        stage = new TransactionStage(DEFAULT_STAGE);
        address = new Address(DEFAULT_ADDRESS);
        tags = new HashSet<>();
        meeting = Optional.empty();
        remark = new Remark(DEFAULT_REMARK);
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        email = personToCopy.getEmail();
        address = personToCopy.getAddress();
        relation = personToCopy.getRelation();
        stage = personToCopy.getStage();
        tags = new HashSet<>(personToCopy.getTags());
        meeting = personToCopy.getMeeting();
        remark = personToCopy.getRemark();
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code Person} that we are building.
     */
    public PersonBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Person} that we are building.
     */
    public PersonBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Sets the {@code Relation} of the {@code Person} that we are building.
     */
    public PersonBuilder withRelation(String relation) {
        this.relation = new Relation(relation);
        return this;
    }

    /**
     * Sets the {@code TransactionStage} of the {@code Person} that we are building.
     */
    public PersonBuilder withStage(String stage) {
        this.stage = new TransactionStage(stage);
        return this;
    }

    /** Sets the optional {@code Meeting} of the {@code Person} that we are building. */
    public PersonBuilder withMeeting(String meeting) {
        this.meeting = Optional.of(new Meeting(meeting));
        return this;
    }

    /** Sets the {@code Remark} of the {@code Person} that we are building. */
    public PersonBuilder withRemark(String remark) {
        this.remark = new Remark(remark);
        return this;
    }

    public Person build() {
        return new Person(name, phone, email, address, relation, stage, remark, tags);
    }

}
