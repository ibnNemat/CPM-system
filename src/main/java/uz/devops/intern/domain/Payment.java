package uz.devops.intern.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @DecimalMin(value = "0")
    @Column(name = "paid_money", nullable = false)
    private Double paidMoney;

    @DecimalMin(value = "10000")
    @Column(name = "payment_for_period", nullable = false)
    private Double paymentForPeriod;

    @Column(name = "is_payed", nullable = false)
    private Boolean isPayed;

    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "started_period", nullable = false)
    private LocalDate startedPeriod;

    @Column(name = "finished_period")
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate finishedPeriod;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "groups" }, allowSetters = true)
    private Customers customer;

    @ManyToOne
    @JsonIgnoreProperties(value = { "groups" }, allowSetters = true)
    private Services service;
    @ManyToOne
    @JsonIgnoreProperties(value = { "customers", "organization", "services" }, allowSetters = true)
    private Groups group;

    public Groups getGroup() {
        return group;
    }

    public void setGroup(Groups group) {
        this.group = group;
    }

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

    public Double getPaidMoney() {
        return this.paidMoney;
    }

    public Payment paidMoney(Double paidMoney) {
        this.setPaidMoney(paidMoney);
        return this;
    }

    public void setPaidMoney(Double paidMoney) {
        this.paidMoney = paidMoney;
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

    public Payment Group(Groups group) {
        this.setGroup(group);
        return this;
    }

    public Payment Service(Services service) {
        this.setService(service);
        return this;
    }
    public void setIsPayed(Boolean isPayed) {
        this.isPayed = isPayed;
    }

    public LocalDate getStartedPeriod() {
        return this.startedPeriod;
    }

    public Payment startedPeriod(LocalDate startedPeriod) {
        this.setStartedPeriod(startedPeriod);
        return this;
    }

    public void setStartedPeriod(LocalDate startedPeriod) {
        this.startedPeriod = startedPeriod;
    }

    public LocalDate getFinishedPeriod() {
        return this.finishedPeriod;
    }

    public Payment finishedPeriod(LocalDate finishedPeriod) {
        this.setFinishedPeriod(finishedPeriod);
        return this;
    }

    public void setFinishedPeriod(LocalDate finishedPeriod) {
        this.finishedPeriod = finishedPeriod;
    }

    public Customers getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customers customers) {
        this.customer = customers;
    }

    public Payment customer(Customers customers) {
        this.setCustomer(customers);
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
            "id=" + id +
            ", paidMoney=" + paidMoney +
            ", paymentForPeriod=" + paymentForPeriod +
            ", isPayed=" + isPayed +
            ", startedPeriod=" + startedPeriod +
            ", finishedPeriod=" + finishedPeriod +
            ", customer=" + customer +
            ", service=" + service +
            ", group=" + group +
            '}';
    }
}
