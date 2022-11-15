package uz.devops.intern.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Services.
 */
@Entity
@Table(name = "services")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Services implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "price", nullable = false)
    private Double price;

    @NotNull
    @Column(name = "period", nullable = false)
    private String period;

    @NotNull
    @Column(name = "count_period", nullable = false)
    private Integer countPeriod;

    @ManyToMany(mappedBy = "services")
    @JsonIgnoreProperties(value = { "groups", "services" }, allowSetters = true)
    private Set<Customers> users = new HashSet<>();

    @ManyToMany(mappedBy = "services")
    @JsonIgnoreProperties(value = { "services", "organization", "users" }, allowSetters = true)
    private Set<Groups> groups = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Services id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Services name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return this.price;
    }

    public Services price(Double price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPeriod() {
        return this.period;
    }

    public Services period(String period) {
        this.setPeriod(period);
        return this;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Integer getCountPeriod() {
        return this.countPeriod;
    }

    public Services countPeriod(Integer countPeriod) {
        this.setCountPeriod(countPeriod);
        return this;
    }

    public void setCountPeriod(Integer countPeriod) {
        this.countPeriod = countPeriod;
    }

    public Set<Customers> getUsers() {
        return this.users;
    }

    public void setUsers(Set<Customers> customers) {
        if (this.users != null) {
            this.users.forEach(i -> i.removeServices(this));
        }
        if (customers != null) {
            customers.forEach(i -> i.addServices(this));
        }
        this.users = customers;
    }

    public Services users(Set<Customers> customers) {
        this.setUsers(customers);
        return this;
    }

    public Services addUsers(Customers customers) {
        this.users.add(customers);
        customers.getServices().add(this);
        return this;
    }

    public Services removeUsers(Customers customers) {
        this.users.remove(customers);
        customers.getServices().remove(this);
        return this;
    }

    public Set<Groups> getGroups() {
        return this.groups;
    }

    public void setGroups(Set<Groups> groups) {
        if (this.groups != null) {
            this.groups.forEach(i -> i.removeServices(this));
        }
        if (groups != null) {
            groups.forEach(i -> i.addServices(this));
        }
        this.groups = groups;
    }

    public Services groups(Set<Groups> groups) {
        this.setGroups(groups);
        return this;
    }

    public Services addGroups(Groups groups) {
        this.groups.add(groups);
        groups.getServices().add(this);
        return this;
    }

    public Services removeGroups(Groups groups) {
        this.groups.remove(groups);
        groups.getServices().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Services)) {
            return false;
        }
        return id != null && id.equals(((Services) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Services{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", price=" + getPrice() +
            ", period='" + getPeriod() + "'" +
            ", countPeriod=" + getCountPeriod() +
            "}";
    }
}
