package uz.devops.intern.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link uz.devops.intern.domain.PaymentHistory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentHistoryDTO implements Serializable {

    private Long id;

    private String organizationName;

    private String groupName;

    private String serviceName;
    private Double sum;
    private LocalDate createdAt;
    private CustomersDTO customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public CustomersDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomersDTO customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentHistoryDTO)) {
            return false;
        }

        PaymentHistoryDTO paymentHistoryDTO = (PaymentHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paymentHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentHistoryDTO{" +
            "id=" + getId() +
            ", organizationName='" + getOrganizationName() + "'" +
            ", groupName='" + getGroupName() + "'" +
            ", serviceName='" + getServiceName() + "'" +
            ", sum=" + getSum() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", customer=" + getCustomer() +
            "}";
    }
}
