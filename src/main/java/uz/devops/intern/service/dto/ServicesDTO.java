package uz.devops.intern.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;
import uz.devops.intern.domain.enumeration.PeriodType;
import uz.devops.intern.domain.enumeration.ServiceType;

/**
 * A DTO for the {@link uz.devops.intern.domain.Services} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServicesDTO implements Serializable {

    private Long id;

    @NotNull
    private ServiceType serviceType;

    @NotNull
    private Double price;

    @NotNull
    private PeriodType periodType;

    @NotNull
    private Integer countPeriod;

    private GroupsDTO group;

    private Set<CustomersDTO> users = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public Integer getCountPeriod() {
        return countPeriod;
    }

    public void setCountPeriod(Integer countPeriod) {
        this.countPeriod = countPeriod;
    }

    public GroupsDTO getGroup() {
        return group;
    }

    public void setGroup(GroupsDTO group) {
        this.group = group;
    }

    public Set<CustomersDTO> getUsers() {
        return users;
    }

    public void setUsers(Set<CustomersDTO> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServicesDTO)) {
            return false;
        }

        ServicesDTO servicesDTO = (ServicesDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, servicesDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServicesDTO{" +
            "id=" + getId() +
            ", serviceType='" + getServiceType() + "'" +
            ", price=" + getPrice() +
            ", periodType='" + getPeriodType() + "'" +
            ", countPeriod=" + getCountPeriod() +
            ", group=" + getGroup() +
            ", users=" + getUsers() +
            "}";
    }
}
