package uz.devops.intern.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Groups.
 */
@Entity
@Table(name = "groups")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Groups implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotNull
    @Column(name = "group_owner_name", nullable = false)
    private String groupOwnerName;

    @ManyToOne
    @JsonIgnoreProperties(value = { "groups" }, allowSetters = true)
    private Organization organization;

    @ManyToMany(mappedBy = "groups")
    @JsonIgnoreProperties(value = { "user", "groups", "services" }, allowSetters = true)
    private Set<Customers> users = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Groups id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Groups name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupOwnerName() {
        return this.groupOwnerName;
    }

    public Groups groupOwnerName(String groupOwnerName) {
        this.setGroupOwnerName(groupOwnerName);
        return this;
    }

    public void setGroupOwnerName(String groupOwnerName) {
        this.groupOwnerName = groupOwnerName;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Groups organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Set<Customers> getUsers() {
        return this.users;
    }

    public void setUsers(Set<Customers> customers) {
        if (this.users != null) {
            this.users.forEach(i -> i.removeGroups(this));
        }
        if (customers != null) {
            customers.forEach(i -> i.addGroups(this));
        }
        this.users = customers;
    }

    public Groups users(Set<Customers> customers) {
        this.setUsers(customers);
        return this;
    }

    public Groups addUsers(Customers customers) {
        this.users.add(customers);
        customers.getGroups().add(this);
        return this;
    }

    public Groups removeUsers(Customers customers) {
        this.users.remove(customers);
        customers.getGroups().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Groups)) {
            return false;
        }
        return id != null && id.equals(((Groups) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Groups{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", groupOwnerName='" + getGroupOwnerName() + "'" +
            "}";
    }
}
