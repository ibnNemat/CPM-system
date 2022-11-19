package uz.devops.intern.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Customers.
 */
@Entity
@Table(name = "customers")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Customers implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @NotNull
    @Column(name = "account", nullable = false)
    private Double account;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @ManyToMany(mappedBy = "users")
    @JsonIgnoreProperties(value = { "organization", "users" }, allowSetters = true)
    private Set<Groups> groups = new HashSet<>();

    @ManyToMany(mappedBy = "users")
    @JsonIgnoreProperties(value = { "group", "users" }, allowSetters = true)
    private Set<Services> services = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Customers id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public Customers username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public Customers password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Customers phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getAccount() {
        return this.account;
    }

    public Customers account(Double account) {
        this.setAccount(account);
        return this;
    }

    public void setAccount(Double account) {
        this.account = account;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Customers user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Groups> getGroups() {
        return this.groups;
    }

    public void setGroups(Set<Groups> groups) {
        this.groups = groups;
    }

    public Customers groups(Set<Groups> groups) {
        this.setGroups(groups);
        return this;
    }

    public Customers addGroups(Groups groups) {
        this.groups.add(groups);
        groups.getUsers().add(this);
        return this;
    }

    public Customers removeGroups(Groups groups) {
        this.groups.remove(groups);
        groups.getUsers().remove(this);
        return this;
    }

    public Set<Services> getServices() {
        return this.services;
    }

    public void setServices(Set<Services> services) {
        this.services = services;
    }

    public Customers services(Set<Services> services) {
        this.setServices(services);
        return this;
    }

    public Customers addServices(Services services) {
        this.services.add(services);
        services.getUsers().add(this);
        return this;
    }

    public Customers removeServices(Services services) {
        this.services.remove(services);
        services.getUsers().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customers)) {
            return false;
        }
        return id != null && id.equals(((Customers) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Customers{" +
            "id=" + getId() +
            ", username='" + getUsername() + "'" +
            ", password='" + getPassword() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", account=" + getAccount() +
            "}";
    }
}
