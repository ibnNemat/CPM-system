package uz.devops.intern.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link uz.devops.intern.domain.TelegramGroup} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TelegramGroupDTO implements Serializable {

    private Long id;

    private String name;

    private Long chatId;

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

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TelegramGroupDTO)) {
            return false;
        }

        TelegramGroupDTO telegramGroupDTO = (TelegramGroupDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, telegramGroupDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TelegramGroupDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", chatId=" + getChatId() +
            "}";
    }
}
