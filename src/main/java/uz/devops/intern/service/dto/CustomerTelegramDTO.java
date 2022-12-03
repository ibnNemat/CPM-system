package uz.devops.intern.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link uz.devops.intern.domain.CustomerTelegram} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerTelegramDTO implements Serializable {

    private Long id;

    private Boolean isBot;

    private String firstname;

    private String lastname;

    private String username;

    private Long telegramId;

    private String phoneNumber;

    private Integer step;

    private Boolean canJoinGroups;

    private String languageCode;

    private Boolean isActive;

    private CustomersDTO customer;

    private Set<TelegramGroupDTO> telegramGroups = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsBot() {
        return isBot;
    }

    public void setIsBot(Boolean isBot) {
        this.isBot = isBot;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Boolean getCanJoinGroups() {
        return canJoinGroups;
    }

    public void setCanJoinGroups(Boolean canJoinGroups) {
        this.canJoinGroups = canJoinGroups;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public CustomersDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomersDTO customer) {
        this.customer = customer;
    }

    public Set<TelegramGroupDTO> getTelegramGroups() {
        return telegramGroups;
    }

    public void setTelegramGroups(Set<TelegramGroupDTO> telegramGroups) {
        this.telegramGroups = telegramGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerTelegramDTO)) {
            return false;
        }

        CustomerTelegramDTO customerTelegramDTO = (CustomerTelegramDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, customerTelegramDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerTelegramDTO{" +
            "id=" + getId() +
            ", isBot='" + getIsBot() + "'" +
            ", firstname='" + getFirstname() + "'" +
            ", lastname='" + getLastname() + "'" +
            ", username='" + getUsername() + "'" +
            ", telegramId=" + getTelegramId() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", step=" + getStep() +
            ", canJoinGroups='" + getCanJoinGroups() + "'" +
            ", languageCode='" + getLanguageCode() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", customer=" + getCustomer() +
            ", telegramGroups=" + getTelegramGroups() +
            "}";
    }
}
