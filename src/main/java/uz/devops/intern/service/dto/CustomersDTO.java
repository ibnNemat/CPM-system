package uz.devops.intern.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link uz.devops.intern.domain.Customers} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomersDTO implements Serializable {

    private Long id;

    @NotNull
    private String username;

    private String password;

    @NotNull
    private String phoneNumber;

    @NotNull
    @DecimalMin(value = "0")
    private Double account;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getAccount() {
        return account;
    }

    public void setAccount(Double account) {
        this.account = account;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomersDTO)) {
            return false;
        }

        CustomersDTO customersDTO = (CustomersDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, customersDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomersDTO{" +
            "id=" + getId() +
            ", username='" + getUsername() + "'" +
            ", password='" + getPassword() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", account=" + getAccount() +
            ", user=" + getUser() +
            "}";
    }
}
