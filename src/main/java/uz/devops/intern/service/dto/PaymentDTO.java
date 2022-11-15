package uz.devops.intern.service.dto;

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
    private Double paymentForPeriod;

    @NotNull
    private Boolean isPayed;

    @NotNull
    private LocalDate startPeriod;

    private CustomersDTO user;

    private ServicesDTO service;

    private GroupsDTO group;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(LocalDate startPeriod) {
        this.startPeriod = startPeriod;
    }

    public CustomersDTO getUser() {
        return user;
    }

    public void setUser(CustomersDTO user) {
        this.user = user;
    }

    public ServicesDTO getService() {
        return service;
    }

    public void setService(ServicesDTO service) {
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
            ", paymentForPeriod=" + getPaymentForPeriod() +
            ", isPayed='" + getIsPayed() + "'" +
            ", startPeriod='" + getStartPeriod() + "'" +
            ", user=" + getUser() +
            ", service=" + getService() +
            ", group=" + getGroup() +
            "}";
    }
}
