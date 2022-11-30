package uz.devops.intern.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link uz.devops.intern.domain.TelegramEntity} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TelegramEntityDTO implements Serializable {

    private Long id;

    private Boolean isBot;

    private String firstname;

    private String lastname;

    private String username;

    private Long telegramId;

    private Boolean canJoinGroups;

    private String languageCode;

    private Boolean isActive;

    private UserDTO user;

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
        if (!(o instanceof TelegramEntityDTO)) {
            return false;
        }

        TelegramEntityDTO telegramEntityDTO = (TelegramEntityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, telegramEntityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TelegramEntityDTO{" +
            "id=" + getId() +
            ", isBot='" + getIsBot() + "'" +
            ", firstname='" + getFirstname() + "'" +
            ", lastname='" + getLastname() + "'" +
            ", username='" + getUsername() + "'" +
            ", telegramId=" + getTelegramId() +
            ", canJoinGroups='" + getCanJoinGroups() + "'" +
            ", languageCode='" + getLanguageCode() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
