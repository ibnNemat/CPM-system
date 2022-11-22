package uz.devops.intern.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import uz.devops.intern.domain.enumeration.PeriodType;
import uz.devops.intern.domain.enumeration.ServiceType;

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
    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;
    @NotNull
    @Column(name = "price", nullable = false)
    private Double price;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false)
    private PeriodType periodType;
    @NotNull
    @Column(name = "count_period", nullable = false)
    private Integer countPeriod;
    @ManyToOne
    @JsonIgnoreProperties(value = { "organization", "users" }, allowSetters = true)
    private Groups group;

    @Column(name = "started_period", nullable = false)
    private LocalDate startedPeriod;
    @ManyToMany
    @JoinTable(
        name = "rel_customers__services",
        joinColumns = @JoinColumn(name = "services_id"),
        inverseJoinColumns = @JoinColumn(name = "customers_id")
    )
    @JsonIgnoreProperties(value = { "user", "groups", "services" }, allowSetters = true)
    private Set<Customers> customers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here


    public LocalDate getStartedPeriod() {
        return startedPeriod;
    }

    public void setStartedPeriod(LocalDate startedPeriod) {
        this.startedPeriod = startedPeriod;
    }

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

    public ServiceType getServiceType() {
        return this.serviceType;
    }

    public Services serviceType(ServiceType serviceType) {
        this.setServiceType(serviceType);
        return this;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
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

    public PeriodType getPeriodType() {
        return this.periodType;
    }

    public Services periodType(PeriodType periodType) {
        this.setPeriodType(periodType);
        return this;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
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

    public Groups getGroup() {
        return this.group;
    }

    public void setGroup(Groups groups) {
        this.group = groups;
    }

    public Services group(Groups groups) {
        this.setGroup(groups);
        return this;
    }

    public Set<Customers> getCustomers() {
        return this.customers;
    }

    public void setCustomers(Set<Customers> customers) {
        if (this.customers != null) {
            this.customers.forEach(i -> i.removeServices(this));
        }
        if (customers != null) {
            customers.forEach(i -> i.addServices(this));
        }
        this.customers = customers;
    }

    public Services users(Set<Customers> customers) {
        this.setCustomers(customers);
        return this;
    }

    public Services addUsers(Customers customers) {
        this.customers.add(customers);
        customers.getServices().add(this);
        return this;
    }

    public Services removeUsers(Customers customers) {
        this.customers.remove(customers);
        customers.getServices().remove(this);
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
            "id=" + id +
            ", serviceType=" + serviceType +
            ", price=" + price +
            ", periodType=" + periodType +
            ", countPeriod=" + countPeriod +
            ", group=" + group +
            ", startedPeriod=" + startedPeriod +
            ", users=" + customers +
            '}';
    }
}
