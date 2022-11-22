package uz.devops.intern.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import uz.devops.intern.domain.enumeration.PeriodType;

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
    @DecimalMin(value = "10000")
    @Column(name = "price", nullable = false)
    private Double price;

    @NotNull
    @Column(name = "started_period", nullable = false)
    private LocalDate startedPeriod;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false)
    private PeriodType periodType;

    @NotNull
    @Min(value = 1)
    @Column(name = "count_period", nullable = false)
    private Integer countPeriod;

    @ManyToMany
    @JoinTable(
        name = "rel_services__groups",
        joinColumns = @JoinColumn(name = "services_id"),
        inverseJoinColumns = @JoinColumn(name = "groups_id")
    )
    @JsonIgnoreProperties(value = { "customers", "organization", "services" }, allowSetters = true)
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

    public LocalDate getStartedPeriod() {
        return this.startedPeriod;
    }

    public Services startedPeriod(LocalDate startedPeriod) {
        this.setStartedPeriod(startedPeriod);
        return this;
    }

    public void setStartedPeriod(LocalDate startedPeriod) {
        this.startedPeriod = startedPeriod;
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

    public Set<Groups> getGroups() {
        return this.groups;
    }

    public void setGroups(Set<Groups> groups) {
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
            ", startedPeriod='" + getStartedPeriod() + "'" +
            ", periodType='" + getPeriodType() + "'" +
            ", countPeriod=" + getCountPeriod() +
            "}";
    }
}
