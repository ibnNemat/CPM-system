package uz.devops.intern.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;

/**
 * A PaymentHistory.
 */
@Entity
@Table(name = "payment_history")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "sum")
    private Double sum;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "groups", "services" }, allowSetters = true)
    private Customers customer;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PaymentHistory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganizationName() {
        return this.organizationName;
    }

    public PaymentHistory organizationName(String organizationName) {
        this.setOrganizationName(organizationName);
        return this;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public PaymentHistory groupName(String groupName) {
        this.setGroupName(groupName);
        return this;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public PaymentHistory serviceName(String serviceName) {
        this.setServiceName(serviceName);
        return this;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Double getSum() {
        return this.sum;
    }

    public PaymentHistory sum(Double sum) {
        this.setSum(sum);
        return this;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public LocalDate getCreatedAt() {
        return this.createdAt;
    }

    public PaymentHistory createdAt(LocalDate createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public Customers getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customers customers) {
        this.customer = customers;
    }

    public PaymentHistory customer(Customers customers) {
        this.setCustomer(customers);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentHistory)) {
            return false;
        }
        return id != null && id.equals(((PaymentHistory) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentHistory{" +
            "id=" + getId() +
            ", organizationName='" + getOrganizationName() + "'" +
            ", groupName='" + getGroupName() + "'" +
            ", serviceName='" + getServiceName() + "'" +
            ", sum=" + getSum() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
