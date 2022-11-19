package uz.devops.intern.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

import uz.devops.intern.domain.Customers;
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
    @NotNull
    private LocalDate startedPeriod;
    private GroupsDTO group;

    public LocalDate getStartedPeriod() {
        return startedPeriod;
    }

    public void setStartedPeriod(LocalDate startedPeriod) {
        this.startedPeriod = startedPeriod;
    }

    private Set<CustomersDTO> users = new HashSet<>();

    public Set<CustomersDTO> getUsers() {
        return users;
    }

    public void setUsers(Set<CustomersDTO> users) {
        this.users = users;
    }

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
            "id=" + id +
            ", serviceType=" + serviceType +
            ", price=" + price +
            ", periodType=" + periodType +
            ", countPeriod=" + countPeriod +
            ", group=" + group +
            ", users=" + users +
            '}';
    }
}
