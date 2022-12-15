package uz.devops.intern.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link uz.devops.intern.domain.BotToken} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotTokenDTO implements Serializable {

    private Long id;

    @NotNull
    private String username;

    @NotNull
    private Long telegramId;

    @NotNull
    private String token;

    private UserDTO createdBy;

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

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserDTO createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BotTokenDTO)) {
            return false;
        }

        BotTokenDTO botTokenDTO = (BotTokenDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, botTokenDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BotTokenDTO{" +
            "id=" + getId() +
            ", username='" + getUsername() + "'" +
            ", telegramId=" + getTelegramId() +
            ", token='" + getToken() + "'" +
            ", createdBy=" + getCreatedBy() +
            "}";
    }
}
