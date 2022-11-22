package uz.devops.intern.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;
import uz.devops.intern.domain.enumeration.PeriodType;

/**
 * A DTO for the {@link uz.devops.intern.domain.Services} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServicesDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    @DecimalMin(value = "10000")
    private Double price;

    @NotNull
    private LocalDate startedPeriod;

    @NotNull
    private PeriodType periodType;

    @NotNull
    @Min(value = 1)
    private Integer countPeriod;

    private Set<GroupsDTO> groups = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDate getStartedPeriod() {
        return startedPeriod;
    }

    public void setStartedPeriod(LocalDate startedPeriod) {
        this.startedPeriod = startedPeriod;
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

    public Set<GroupsDTO> getGroups() {
        return groups;
    }

    public void setGroups(Set<GroupsDTO> groups) {
        this.groups = groups;
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
            ", name='" + getName() + "'" +
            ", price=" + getPrice() +
            ", startedPeriod='" + getStartedPeriod() + "'" +
            ", periodType='" + getPeriodType() + "'" +
            ", countPeriod=" + getCountPeriod() +
            ", groups=" + getGroups() +
            "}";
    }
}
