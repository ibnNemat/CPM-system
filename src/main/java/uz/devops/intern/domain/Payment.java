package uz.devops.intern.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Payment.
 */
@Entity
@Table(name = "payment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "payment_for_period", nullable = false)
    private Double paymentForPeriod;

    @NotNull
    @Column(name = "is_payed", nullable = false)
    private Boolean isPayed;

    @NotNull
    @Column(name = "start_period", nullable = false)
    private LocalDate startPeriod;

    @ManyToOne
    @JsonIgnoreProperties(value = { "groups", "services" }, allowSetters = true)
    private Customers user;

    @ManyToOne
    @JsonIgnoreProperties(value = { "users", "groups" }, allowSetters = true)
    private Services service;

    @ManyToOne
    @JsonIgnoreProperties(value = { "services", "organization", "users" }, allowSetters = true)
    private Groups group;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Payment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPaymentForPeriod() {
        return this.paymentForPeriod;
    }

    public Payment paymentForPeriod(Double paymentForPeriod) {
        this.setPaymentForPeriod(paymentForPeriod);
        return this;
    }

    public void setPaymentForPeriod(Double paymentForPeriod) {
        this.paymentForPeriod = paymentForPeriod;
    }

    public Boolean getIsPayed() {
        return this.isPayed;
    }

    public Payment isPayed(Boolean isPayed) {
        this.setIsPayed(isPayed);
        return this;
    }

    public void setIsPayed(Boolean isPayed) {
        this.isPayed = isPayed;
    }

    public LocalDate getStartPeriod() {
        return this.startPeriod;
    }

    public Payment startPeriod(LocalDate startPeriod) {
        this.setStartPeriod(startPeriod);
        return this;
    }

    public void setStartPeriod(LocalDate startPeriod) {
        this.startPeriod = startPeriod;
    }

    public Customers getUser() {
        return this.user;
    }

    public void setUser(Customers customers) {
        this.user = customers;
    }

    public Payment user(Customers customers) {
        this.setUser(customers);
        return this;
    }

    public Services getService() {
        return this.service;
    }

    public void setService(Services services) {
        this.service = services;
    }

    public Payment service(Services services) {
        this.setService(services);
        return this;
    }

    public Groups getGroup() {
        return this.group;
    }

    public void setGroup(Groups groups) {
        this.group = groups;
    }

    public Payment group(Groups groups) {
        this.setGroup(groups);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }
        return id != null && id.equals(((Payment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Payment{" +
            "id=" + getId() +
            ", paymentForPeriod=" + getPaymentForPeriod() +
            ", isPayed='" + getIsPayed() + "'" +
            ", startPeriod='" + getStartPeriod() + "'" +
            "}";
    }
}
