package uz.devops.intern.service.dto;

import uz.devops.intern.domain.Services;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link uz.devops.intern.domain.Payment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentDTO implements Serializable {

    private Long id;

    @NotNull
    private Double payedMoney;

    @NotNull
    private Double paymentForPeriod;

    @NotNull
    private Boolean isPayed;

    @NotNull
    private LocalDate startedPeriod;

    @NotNull
    private LocalDate finishedPeriod;

    private CustomersDTO customer;

    private ServicesDTO service;

    private GroupsDTO group;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPayedMoney() {
        return payedMoney;
    }

    public void setPayedMoney(Double payedMoney) {
        this.payedMoney = payedMoney;
    }

    public Double getPaymentForPeriod() {
        return paymentForPeriod;
    }

    public void setPaymentForPeriod(Double paymentForPeriod) {
        this.paymentForPeriod = paymentForPeriod;
    }

    public Boolean getIsPayed() {
        return isPayed;
    }

    public void setIsPayed(Boolean isPayed) {
        this.isPayed = isPayed;
    }

    public LocalDate getStartedPeriod() {
        return startedPeriod;
    }

    public void setStartedPeriod(LocalDate startedPeriod) {
        this.startedPeriod = startedPeriod;
    }

    public LocalDate getFinishedPeriod() {
        return finishedPeriod;
    }

    public void setFinishedPeriod(LocalDate finishedPeriod) {
        this.finishedPeriod = finishedPeriod;
    }

    public CustomersDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomersDTO customer) {
        this.customer = customer;
    }

    public ServicesDTO getService() {
        return service;
    }

    public void setService(Services service) {
        this.service = service;
    }

    public GroupsDTO getGroup() {
        return group;
    }

    public void setGroup(GroupsDTO group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentDTO)) {
            return false;
        }

        PaymentDTO paymentDTO = (PaymentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paymentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentDTO{" +
            "id=" + getId() +
            ", payedMoney=" + getPayedMoney() +
            ", paymentForPeriod=" + getPaymentForPeriod() +
            ", isPayed='" + getIsPayed() + "'" +
            ", startedPeriod='" + getStartedPeriod() + "'" +
            ", finishedPeriod='" + getFinishedPeriod() + "'" +
            ", customer=" + getCustomer() +
            ", service=" + getService() +
            ", group=" + getGroup() +
            "}";
    }
}
