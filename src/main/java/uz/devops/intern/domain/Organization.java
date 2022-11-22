package uz.devops.intern.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Organization.
 */
@Entity
@Table(name = "organization")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Organization implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "org_owner_name")
    private String orgOwnerName;

    @OneToMany(mappedBy = "organization")
    @JsonIgnoreProperties(value = { "customers", "organization", "services" }, allowSetters = true)
    private Set<Groups> groups = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Organization id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Organization name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrgOwnerName() {
        return this.orgOwnerName;
    }

    public Organization orgOwnerName(String orgOwnerName) {
        this.setOrgOwnerName(orgOwnerName);
        return this;
    }

    public void setOrgOwnerName(String orgOwnerName) {
        this.orgOwnerName = orgOwnerName;
    }

    public Set<Groups> getGroups() {
        return this.groups;
    }

    public void setGroups(Set<Groups> groups) {
        if (this.groups != null) {
            this.groups.forEach(i -> i.setOrganization(null));
        }
        if (groups != null) {
            groups.forEach(i -> i.setOrganization(this));
        }
        this.groups = groups;
    }

    public Organization groups(Set<Groups> groups) {
        this.setGroups(groups);
        return this;
    }

    public Organization addGroups(Groups groups) {
        this.groups.add(groups);
        groups.setOrganization(this);
        return this;
    }

    public Organization removeGroups(Groups groups) {
        this.groups.remove(groups);
        groups.setOrganization(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Organization)) {
            return false;
        }
        return id != null && id.equals(((Organization) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Organization{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", orgOwnerName='" + getOrgOwnerName() + "'" +
            "}";
    }
}
